package com.kafein.ticket_management.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.aop.Audit;
import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.request.RequestTicketDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.exception.BusinessException;
import com.kafein.ticket_management.exception.ResourceNotFoundException;
import com.kafein.ticket_management.mapper.TicketMapper;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;
import com.kafein.ticket_management.repository.TicketRepository;

import jakarta.transaction.Transactional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final UserService userService;

    public TicketService(TicketRepository ticketRepository, UserService userService, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.userService = userService;
    }

    @Transactional
    @Audit(action = "TICKET_CREATED")
    public ResponseCreateTicketDto createTicket(RequestCreateTicketDto requestCreateTicketDto) {

        User user = userService.getUserById(requestCreateTicketDto.assignedToId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User", "id", requestCreateTicketDto.assignedToId()));

        Ticket ticket = Ticket.builder()
                .title(requestCreateTicketDto.title())
                .description(requestCreateTicketDto.description())
                .priority(requestCreateTicketDto.priority())
                .assignedTo(user)
                .build();

        ticketRepository.save(ticket);

        return ticketMapper.toCreateTicketDto(ticket);

    }

    public List<ResponseTicketDto> getAllTickets() {

        if (userService.isAdmin()) {
            return ticketRepository.findAll()
                    .stream()
                    .map((ticket) -> ticketMapper.toDto(ticket))
                    .toList();
        } else {
            throw new AccessDeniedException("Bu işlemi yapmak için ADMIN yetkisine sahip olmalısınız!");
        }
    }

    @Transactional
    @Audit(action = "TICKET_DELETE_ALL")
    public void deleteAllTickets() {
        if (userService.isAdmin()) {
            ticketRepository.deleteAll();
        } else {
            throw new AccessDeniedException("Bu işlemi yapmak için ADMIN yetkisine sahip olmalısınız!");
        }
    }

    @Transactional
    @Audit(action = "TICKET_DELETE")
    public void deleteTicketById(UUID id) {
        ticketRepository.deleteById(id);
    }

    public Page<ResponseTicketDto> filterTickets(TicketStatus status, TicketPriority priority,
            UUID assignedToId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdBy").descending());

        // Dinamik sorgu oluşturma
        Specification<Ticket> spec = Specification.where((root, query, cb) -> cb.conjunction());
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }
        if (assignedToId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignedTo").get("id"), assignedToId));
        }
        return ticketRepository.findAll(spec, pageable)
                .map(ticketMapper::toDto);
    }

    @Transactional
    @Audit(action = "TICKET_UPDATE_STATUS")
    public ResponseTicketDto updateTicketStatus(UUID ticketId, TicketStatus status) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (ticket.getStatus() == TicketStatus.DONE) {
            throw new BusinessException("Kapanmış bir biletin durumunu değiştiremezsiniz!");
        }

        if (ticket.getAssignedTo().getId().equals(userService.getCurrentUser().getId())) {

            // Statü geçişi geçerli mi kontrolü
            boolean isValidTransition = (ticket.getStatus() == TicketStatus.OPEN && status == TicketStatus.IN_PROGRESS)
                    ||
                    (ticket.getStatus() == TicketStatus.IN_PROGRESS && status == TicketStatus.DONE);

            if (!isValidTransition) {
                throw new BusinessException("Geçersiz statü geçişi!");
            }

            ticket.setStatus(status);
            ticketRepository.save(ticket);
            return ticketMapper.toDto(ticket);
        } else {
            throw new AccessDeniedException("Bu bilet sadece atanmış kullanıcı tarafından güncellebilir!");
        }
    }

    @Transactional // TODO : İŞ KURALLARINI GELİŞTİR
    @Audit(action = "TICKET_UPDATE")
    public ResponseTicketDto updateTicket(UUID ticketId, RequestTicketDto requestTicketDto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (ticket.getCreatedBy().getId().equals(userService.getCurrentUser().getId())) {

            ticket.setTitle(requestTicketDto.title());
            ticket.setDescription(requestTicketDto.description());
            ticket.setPriority(requestTicketDto.priority());

            if (requestTicketDto.status() != null && !ticket.getStatus().equals(requestTicketDto.status())) {
                ticket.setStatus(updateTicketStatus(ticketId, requestTicketDto.status()).status());
            }

            if (requestTicketDto.assignedToId() != null) {

                User newAssignee = userService.getUserById(requestTicketDto.assignedToId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("User", "id", requestTicketDto.assignedToId()));

                ticket.setAssignedTo(newAssignee);

            }

        } else {
            throw new BusinessException("Bu bileti sadece oluşturan kullanıcı güncelleyebilir!");
        }

        return ticketMapper.toDto(ticketRepository.save(ticket));

    }

    public Optional<Ticket> getTicketById(UUID ticketId) {
        return ticketRepository.findById(ticketId);

    }

    public Long totalTicketCount() {
        return ticketRepository.count();
    }

    public Map<TicketStatus, Long> getEachStatusTotalTicketsCount() {
        List<Object[]> results = ticketRepository.countTicketsByStatusRaw();
        Map<TicketStatus, Long> statusMap = new HashMap<>();

        // Default olarak statü bulunmayanları da koruma altında alıyoruz özellikle UI kullanımında önemli
        for (TicketStatus status : TicketStatus.values()) {
            statusMap.put(status, 0L);
        }

        for (Object[] result : results) {
            TicketStatus status = (TicketStatus) result[0];
            Long count = (Long) result[1];
            statusMap.put(status, count);
        }

        return statusMap;
    }

    public List<ResponseTicketDto> getLast5Tickets() {
        return ticketRepository.findTop5ByOrderByCreatedAtDateDesc()
                .stream()
                .map((ticket) -> ticketMapper.toDto(ticket))
                .toList();
    }

}
