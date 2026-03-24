package com.kafein.ticket_management.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kafein.ticket_management.dto.response.ResponseTicketDto;
import com.kafein.ticket_management.mapper.TicketMapper;
import com.kafein.ticket_management.model.Ticket;
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;
import com.kafein.ticket_management.repository.TicketRepository;

@Service
public class TicketAnalyticsService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    public TicketAnalyticsService(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
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

    public Map<TicketPriority, Long> getTotalPriority() {
        List<Object[]> results = ticketRepository.countTicketsByPriorityRaw();
        Map<TicketPriority, Long> priorityMap = new HashMap<>();

        // Default olarak priority bulunmayanları da koruma altında alıyoruz özellikle
        // UI
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

        return Math.round(averageHours * 100.0) / 100.0;
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
