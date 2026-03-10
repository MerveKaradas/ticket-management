package com.kafein.ticket_management.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.kafein.ticket_management.dto.request.TicketStatusUpdateRequestDto;
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
        return ticketRepository.findAll()
                .stream()
                .map((ticket) -> ticketMapper.toDto(ticket))
                .toList();
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

        if (userService.isAdmin()) {
            if (!ticketRepository.existsById(id)) {
                throw new ResourceNotFoundException("Ticket", "id", id);
            }
            ticketRepository.deleteById(id);
        } else {
            throw new AccessDeniedException("Bu işlemi yapmak için ADMIN yetkisine sahip olmalısınız!");
        }
    }

    public Page<ResponseTicketDto> filterTickets(String title, TicketStatus status, TicketPriority priority,
            UUID assignedToId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAtDate").descending()); // nesne kullanma

        Specification<Ticket> spec = Specification.where(null);

        if (title != null && !title.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }
        if (assignedToId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("assignedTo").get("id"), assignedToId));
        }

        return ticketRepository.findAll(spec, pageable).map(ticketMapper::toDto);
    }

    @Transactional
    @Audit(action = "TICKET_UPDATE_STATUS")
    public ResponseTicketDto updateTicketStatus(UUID ticketId, TicketStatusUpdateRequestDto requestStatusDto) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (ticket.getAssignedTo().getId().equals(userService.getCurrentUser().getId())) {

            if (!isValidTransition(ticket.getStatus(),requestStatusDto.status())) {
                throw new BusinessException("Geçersiz statü geçişi!");
            }

            ticket.setStatus(requestStatusDto.status());
            ticketRepository.save(ticket);
            return ticketMapper.toDto(ticket);
        } else {
            throw new AccessDeniedException("Bu bilet sadece atanmış kullanıcı tarafından güncellebilir!");
        }
    }

    private boolean isValidTransition(TicketStatus ticketStatus, TicketStatus requestStatus) {
        return (ticketStatus == TicketStatus.REOPENED && requestStatus == TicketStatus.IN_PROGRESS)
                || (ticketStatus == TicketStatus.OPEN && requestStatus == TicketStatus.IN_PROGRESS)
                || (ticketStatus == TicketStatus.IN_PROGRESS && requestStatus == TicketStatus.DONE)
                || (ticketStatus == TicketStatus.DONE && requestStatus == TicketStatus.REOPENED);

    }

    @Transactional
    @Audit(action = "TICKET_UPDATE")
    public ResponseTicketDto updateTicket(UUID ticketId, RequestTicketDto requestTicketDto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (ticket.getCreatedBy().getId().equals(userService.getCurrentUser().getId())) {

            if (requestTicketDto.assignedToId() != null) {

                User newAssignee = userService.getUserById(requestTicketDto.assignedToId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("User", "id", requestTicketDto.assignedToId()));

                ticket.setAssignedTo(newAssignee);

                if (ticket.getStatus() == TicketStatus.DONE) {
                    ticket.setStatus(TicketStatus.REOPENED);
                } else {
                    if (requestTicketDto.status() != null && !ticket.getStatus().equals(requestTicketDto.status())) {
                        if (!isValidTransition(ticket.getStatus(), requestTicketDto.status())) {
                            throw new BusinessException("Geçersiz statü geçişi!");
                        } else {
                            ticket.setStatus(requestTicketDto.status());
                        }
                    }
                }

            }

        } else {
            throw new BusinessException("Bu bileti sadece oluşturan kullanıcı güncelleyebilir!");
        }

        ticket.setTitle(requestTicketDto.title());
        ticket.setDescription(requestTicketDto.description());
        ticket.setPriority(requestTicketDto.priority());

        ticketRepository.save(ticket);

        return ticketMapper.toDto(ticket);
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

        // Default olarak statü bulunmayanları da koruma altında alıyoruz özellikle UI
        // kullanımında önemli
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

    public ResponseTicketDto getTicket(UUID id) {
        return ticketRepository.findById(id)
                .map((ticket) -> ticketMapper.toDto(ticket))
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
    }

    public Map<TicketPriority, Long> getTotalPriority() {
        List<Object[]> results = ticketRepository.countTicketsByPriorityRaw();
        Map<TicketPriority, Long> priorityMap = new HashMap<>();

        // Default olarak priority bulunmayanları da koruma altında alıyoruz özellikle UI
        // kullanımında önemli
        for (TicketPriority priority : TicketPriority.values()) {
            priorityMap.put(priority, 0L);
        }

        for (Object[] result : results) {
            TicketPriority priority = (TicketPriority) result[0];
            Long count = (Long) result[1];
            priorityMap.put(priority, count);
        }

        return priorityMap;
    }

    public Double calculateAverageResolveTime() {
        // Sadece DONE 
        List<Ticket> resolvedTickets = ticketRepository.findAllByStatus(TicketStatus.DONE);

        if (resolvedTickets.isEmpty()) {
            return 0.0;
        }

        // Oluşturulma ve son güncellenme arasındaki farkı hesabı
        long totalMinutes = resolvedTickets.stream()
                .mapToLong(ticket -> {
                    return java.time.Duration.between(
                            ticket.getCreatedAtDate(),
                            ticket.getUpdatedDate()).toMinutes();
                })
                .sum();

        double averageMinutes = (double) totalMinutes / resolvedTickets.size();
        double averageHours = averageMinutes / 60.0;

        return Math.round(averageHours * 10.0) / 10.0;
    }

    public Map<String, Long> getUserWorkloadDistribution() {
        List<Object[]> results = ticketRepository.countTicketsByFullAssigneeName();

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> (Long) result[1]));
    }

    public Map<String, Long> getDailyTrendAnalysis() {
        // Gün başlangıcı
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);

        List<Object[]> results = ticketRepository.countDailyTrendByStatus(startOfDay);

        Map<String, Long> trendMap = new HashMap<>();

        results.forEach(result -> {
            trendMap.put(((TicketStatus) result[0]).name(), (Long) result[1]);
        });

        // Grafikte boşluk kalmasın diye tüm statüleri 0 ile başlatıyoruz
        for (TicketStatus status : TicketStatus.values()) {
            trendMap.putIfAbsent(status.name(), 0L);
        }

        return trendMap;
    }
}
