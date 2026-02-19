package com.kafein.ticket_management.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.request.RequestTicketDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.exception.BusinessException;
import com.kafein.ticket_management.exception.ResourceNotFoundException;
import com.kafein.ticket_management.exception.UnauthorizedException;
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
    public ResponseCreateTicketDto createTicket(RequestCreateTicketDto requestCreateTicketDto) {

        User user = userService.getUserById(requestCreateTicketDto.getAssignedToId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("User", "id", requestCreateTicketDto.getAssignedToId()));

        Ticket ticket = Ticket.builder()
                .title(requestCreateTicketDto.getTitle())
                .description(requestCreateTicketDto.getDescription())
                .priority(requestCreateTicketDto.getPriority())
                .assignedTo(user)
                .build();

        ticketRepository.save(ticket);

        return ticketMapper.toCreateTicketDto(ticket);

    }

    public List<ResponseTicketDto> getAllTickets() {

        return ticketRepository.findAll()
                .stream()
                .map((ticket) -> ticketMapper.toDto(ticket))
                .toList();
    }

    @Transactional
    public void deleteAllTickets() {
        ticketRepository.deleteAll();
    }

    @Transactional
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
    public ResponseTicketDto updateTicketStatus(UUID ticketId, TicketStatus status) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (ticket.getStatus() == TicketStatus.DONE) {
            throw new BusinessException("Kapanmış bir biletin durumunu değiştiremezsiniz!");
        }

        if (ticket.getAssignedTo().getId().equals(getCurrentUserId())) {

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

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new UnauthorizedException();
        }

        User currentUser = (User) authentication.getPrincipal();
        return currentUser.getId();
    }

    @Transactional // TODO : İŞ KURALLARINI GELİŞTİR
    public ResponseTicketDto updateTicket(UUID ticketId, RequestTicketDto requestTicketDto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket","id" ,ticketId));

        if (ticket.getCreatedBy().getId().equals(getCurrentUserId())) {

            ticket.setTitle(requestTicketDto.getTitle());
            ticket.setDescription(requestTicketDto.getDescription());
            ticket.setPriority(requestTicketDto.getPriority());

            if (requestTicketDto.getStatus() != null && !ticket.getStatus().equals(requestTicketDto.getStatus())) {
                ticket.setStatus(updateTicketStatus(ticketId, requestTicketDto.getStatus()).status());
            }

            if (requestTicketDto.getAssignedToId() != null) {

                User newAssignee = userService.getUserById(requestTicketDto.getAssignedToId())
                        .orElseThrow(() -> new ResourceNotFoundException("User","id",requestTicketDto.getAssignedToId()));

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

}
