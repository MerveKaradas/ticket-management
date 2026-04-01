package com.kafein.ticket_management.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.kafein.ticket_management.aop.Audit;
import com.kafein.ticket_management.dto.request.RequestCreateTicketDto;
import com.kafein.ticket_management.dto.request.RequestTicketClaimDto;
import com.kafein.ticket_management.dto.request.RequestTicketDto;
import com.kafein.ticket_management.dto.request.TicketStatusUpdateRequestDto;
import com.kafein.ticket_management.dto.response.ResponseCreateTicketDto;
import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.exception.BusinessException;
import com.kafein.ticket_management.exception.ResourceNotFoundException;
import com.kafein.ticket_management.mapper.TicketMapper;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.User;
import com.kafein.ticket_management.model.enums.Role;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;
import com.kafein.ticket_management.repository.TicketRepository;
import com.kafein.ticket_management.spec.TicketSpecifications;

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
    @CacheEvict(value = "analytics", allEntries = true)
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

    @PreAuthorize("hasRole('ADMIN')")
    public List<ResponseTicketDto> getAllTickets() {

        return ticketRepository.findAll()
                .stream()
                .map((ticket) -> ticketMapper.toDto(ticket))
                .toList();
    }

    @Transactional
    @Audit(action = "TICKET_DELETE_ALL")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "analytics", allEntries = true)
    public void deleteAllTickets() {
        ticketRepository.deleteAll();
    }

    @Transactional
    @Audit(action = "TICKET_DELETE")
    @PreAuthorize("hasRole('ADMIN')")
    @CacheEvict(value = "analytics", allEntries = true)
    public void deleteTicketById(UUID id) {

        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket", "id", id);
        }
        ticketRepository.deleteById(id);
    }

    public Page<ResponseTicketDto> filterTickets(String title, TicketStatus status, TicketPriority priority,
            UUID assignedToId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAtDate").descending()); // nesne kullanma

        Specification<Ticket> spec = Specification.where(TicketSpecifications.hasTitle(title))
                .and(TicketSpecifications.hasStatus(status))
                .and(TicketSpecifications.hasPriority(priority))
                .and(TicketSpecifications.hasAssignedTo(assignedToId));

        return ticketRepository.findAll(spec, pageable).map(ticketMapper::toDto);
    }

    @Transactional
    @Audit(action = "TICKET_UPDATE_STATUS")
    @CacheEvict(value = "analytics", allEntries = true)
    public ResponseTicketDto updateTicketStatus(UUID ticketId, TicketStatusUpdateRequestDto requestStatusDto) {

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (ticket.getAssignedTo().getId().equals(userService.getCurrentUser().getId())) {

            if (!isValidTransition(ticket.getStatus(), requestStatusDto.status())) {
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
    @CacheEvict(value = "analytics", allEntries = true)
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

    public ResponseTicketDto getTicket(UUID id) {
        return ticketRepository.findById(id)
                .map((ticket) -> ticketMapper.toDto(ticket))
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", id));
    }

    @Transactional
    @CacheEvict(value = "analytics", allEntries = true)
    public void updateAllTicketsByDeletedUserId(UUID userId) {

        User systemPool = userService.getSystemPool();

        // Kullanıcının sadece tamamlanmamış biletleri
        List<Ticket> activeTickets = ticketRepository.findAllByassignedTo_IdAndStatusNot(
                userId, TicketStatus.DONE);

        if(activeTickets.isEmpty()){
            return;
        }

        activeTickets.forEach(ticket -> {
            ticket.setStatus(TicketStatus.BACKLOG);
            ticket.setAssignedTo(systemPool);
            String newTitle = "Atama bekliyor!";
            ticket.setTitle(newTitle);
        });

        ticketRepository.saveAll(activeTickets);
    }

    @Transactional
    @Audit(action = "TICKET_CLAIM")
    @CacheEvict(value = "analytics", allEntries = true)
    public ResponseTicketDto claimTicket(UUID ticketId, RequestTicketClaimDto claimDto) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        if (ticket.getAssignedTo().getRole() != Role.SYSTEM) {
            throw new AccessDeniedException("Bu bilet zaten bir kullanıcıya atanmış.");
        }

        ticket.setAssignedTo(userService.getCurrentUser());
        ticket.setTitle(claimDto.newTitle());
        ticket.setStatus(TicketStatus.IN_PROGRESS);

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

}
