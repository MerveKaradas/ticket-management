package com.kafein.ticket_management.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class) // Junit'i Mockito ile genişletiyoruz yoksa mock içleri null kalır
public class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;

    @Spy // Normalde gerçek bir nesne üzerinden çalışır
    private TicketMapper ticketMapper = Mappers.getMapper(TicketMapper.class);
    // INFO : Mockito interfaceleri nasıl enjekte edeceğini bilemez. Classları
    // injectmocks ile enjekte eder ancak MapStruct yapısı interface old. dolayı
    // manuel olarak oluşturmamız gerekiyor.

    @Mock
    private UserService userService;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void createTicket_WhenUserExists_ShouldReturnResponse() {

        // ARRANGE
        UUID userId = UUID.randomUUID();
        User user = createUser(userId);
        RequestCreateTicketDto requestCreateTicketDto = new RequestCreateTicketDto("Title", "Description",
                TicketPriority.LOW, userId);
        Ticket ticket = createTicket(user, requestCreateTicketDto);
        ResponseCreateTicketDto expectedTicketDto = ticketMapper.toCreateTicketDto(ticket);

        given(userService.getUserById(userId)).willReturn(Optional.of(user));
        given(ticketRepository.save(any(Ticket.class))).willReturn(ticket);

        // ACT
        ResponseCreateTicketDto result = ticketService.createTicket(requestCreateTicketDto);

        // ASSERT
        assertEquals(expectedTicketDto.title(), result.title());
        assertEquals(expectedTicketDto.description(), result.description());
        assertEquals(expectedTicketDto.status(), result.status());
        assertEquals(expectedTicketDto.priority(), result.priority());
        assertEquals(expectedTicketDto.assignedTo(), result.assignedTo());
        verify(ticketRepository, times(1)).save(any(Ticket.class));

    }

    @Test
    void createTicket_WhenUserDoesNotExist_ThrowsException() {

        // ARRANGE
        UUID userId = UUID.randomUUID();
        RequestCreateTicketDto requestDto = new RequestCreateTicketDto("Title", "Description", TicketPriority.LOW,
                userId);

        given(userService.getUserById(userId)).willReturn(Optional.empty());

        // ACT ve ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.createTicket(requestDto);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
        // Mapper'a da hiç gidilmemeli
        verifyNoInteractions(ticketMapper);

    }

    @Test
    void deleteTicket_AsAdmin_ShouldSucceed() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();

        given(userService.isAdmin()).willReturn(true);
        given(ticketRepository.existsById(ticketId)).willReturn(true);

        // ACT
        ticketService.deleteTicketById(ticketId);

        // ASSERT
        verify(ticketRepository, times(1)).deleteById(ticketId);

    }

    @Test
    void deleteTicket_AsUser_ThrowsException() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();

        given(userService.isAdmin()).willReturn(false);

        // ACT ve ASSERT

        assertThrows(AccessDeniedException.class, () -> {
            ticketService.deleteTicketById(ticketId);
        });

        verify(ticketRepository, never()).deleteById(ticketId);

    }

    @Test
    void deleteTicket_WhenTicketNotFound_ThrowsException() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();

        given(userService.isAdmin()).willReturn(true);
        given(ticketRepository.existsById(ticketId)).willReturn(false);

        // ACT ve ASSERT

        assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.deleteTicketById(ticketId);
        });

        verify(ticketRepository, never()).deleteById(ticketId);

    }

    @Test
    void deleteAllTickets_AsAdmin_ShouldSucceed() {

        // ARRANGE
        given(userService.isAdmin()).willReturn(true);

        // ACT
        ticketService.deleteAllTickets();

        // ASSERT
        verify(ticketRepository, times(1)).deleteAll();

    }

    @Test
    void deleteAllTickets_AsUser_ThrowsException() {

        // ARRANGE
        given(userService.isAdmin()).willReturn(false);

        // ACT ASSERT
        assertThrows(AccessDeniedException.class, () -> {
            ticketService.deleteAllTickets();
        });

        verify(ticketRepository, never()).deleteAll();

    }

    @Test
    void getAllTickets_AsAdmin_ShouldReturnList() {

        // ARRANGE
        given(userService.isAdmin()).willReturn(true);

        // ACT
        ticketService.getAllTickets();

        // ASSERT
        verify(ticketRepository, times(1)).findAll();
    }

    @Test
    void getAllTickets_AsUser_ThrowsException() {

        // ASSERT
        given(userService.isAdmin()).willReturn(false);

        // ACT ASSERT
        assertThrows(AccessDeniedException.class, () -> {
            ticketService.getAllTickets();
        });

        verify(ticketRepository, never()).findAll();

    }

    @Test
    void updateStatus_WhenUserNotAssigned_ThrowsException() {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();

        User assignee = createUser(assigneeId);
        User currentUser = createUser(otherUserId);
        TicketStatus status = TicketStatus.IN_PROGRESS;
        TicketStatusUpdateRequestDto dtoStatus = new TicketStatusUpdateRequestDto(status);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(assignee)
                .status(status)
                .build();

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);

        // ACT ve ASSERT
        assertThrows(AccessDeniedException.class, () -> {
            ticketService.updateTicketStatus(ticketId, dtoStatus);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));

    }


    @ParameterizedTest
    @CsvSource({
            "OPEN, IN_PROGRESS",
            "REOPENED, IN_PROGRESS",
            "IN_PROGRESS, DONE",
            "REOPENED, IN_PROGRESS",
            "DONE, REOPENED"
    })
    @DisplayName("Geçerli tüm statü geçiş senaryoları başarıyla kaydedilmelidir")
    void updateStatus_WithValidTransitions_ShouldSucceed(TicketStatus initialStatus, TicketStatus targetStatus) {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        User user = createUser(UUID.randomUUID());
        TicketStatusUpdateRequestDto requestStatusDto = new TicketStatusUpdateRequestDto(targetStatus);
        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(user)
                .status(initialStatus)
                .build();

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(user);

        // ACT
        ticketService.updateTicketStatus(ticketId, requestStatusDto);

        // ASSERT
        verify(ticketRepository, times(1)).save(any(Ticket.class));
    }

    @ParameterizedTest
    @CsvSource({
            "OPEN, REOPENED",
            "OPEN, DONE",
            "REOPENED, OPEN",
            "REOPENED, DONE",
            "IN_PROGRESS, REOPENED",
            "IN_PROGRESS, OPEN",
            "DONE, OPEN",
            "DONE, IN_PROGRESS"
    })
    @DisplayName("Geçersiz tüm statü geçiş senaryoları hata dönmelidir")
    void updateStatus_WithInvalidTransition_ThrowsException(TicketStatus initialStatus, TicketStatus targetStatus) {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        User user = createUser(UUID.randomUUID());
        TicketStatusUpdateRequestDto requestStatusDto = new TicketStatusUpdateRequestDto(targetStatus);
        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(user)
                .status(initialStatus)
                .build();

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(user);

        // ACT ve ASSERT
        assertThrows(BusinessException.class, () -> {
        ticketService.updateTicketStatus(ticketId, requestStatusDto);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
    }


    @DisplayName("Bilet güncelleme metodunda güncellenmek istenen biletin bulunamaması durumunda ResourceNotFoundException türünde hata fırlatması.")
    @Test
    void updateTicket_WhenTicketNotFound_ThrowsException() {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();

        given(ticketRepository.findById(ticketId)).willReturn(Optional.empty());

        // ACT ve ASSERT
        assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.updateTicket(ticketId, any(RequestTicketDto.class));
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(ticketMapper);
    }

    @DisplayName("Bilet güncelleme metodunda ticket güncellemek isteyen kişinin mevcut kullanıcı olmaması durumunda BusinessException türünde hata fırlatması.")
    @Test
    void updateTicket_WhenUserNotOwner_ThrowsException() {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        User otherUser = createUser(otherUserId);
        User currentUser = createUser(currentUserId);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .createdBy(otherUser)
                .build();

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);

        // ACT ve ASSERT
        assertThrows(BusinessException.class, () -> {
            ticketService.updateTicket(ticketId, any(RequestTicketDto.class));
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(ticketMapper);
    }

    @DisplayName("Bilet güncelleme metodunda güncellenecek olan ticketin bulunması durumunda ve ticket güncellemek isteyen kişinin ticketi oluşturan kullanıcı olması durumunda ve yeni atanacak kullanıcının olması ve null gelmemesi ve kaydının bulunamaması durumunda ResourceNotFoundException türünde hata fırlatılması")
    @Test
    void updateTicket_WhenNewAssigneeNotFound_ThrowsException() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID oldAssigneeId = UUID.randomUUID();
        UUID newAssigneeId = UUID.randomUUID();

        User oldAssignee = createUser(oldAssigneeId);
        User currentUser = createUser(currentUserId);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(oldAssignee)
                .status(TicketStatus.OPEN)
                .createdBy(currentUser)
                .build();
        RequestTicketDto requestTicketDto = new RequestTicketDto("Title", "Description", TicketStatus.IN_PROGRESS,
                TicketPriority.LOW, newAssigneeId);

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.getUserById(requestTicketDto.assignedToId())).willReturn(Optional.empty());

        // ACT
        assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.updateTicket(ticketId, requestTicketDto);
        });

        // ASSERT
        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoInteractions(ticketMapper);
    }

    @DisplayName("Bilet güncelleme metodunda güncellenecek olan ticketin bulunması durumunda ve ticket güncellemek isteyen kişinin ticketi oluşturan kullanıcı olması durumunda ve yeni atanacak kullanıcının null gelme durumunda kayıt altına alınması")
    @Test
    void updateTicket_WithNullAssignee_ShouldSucceed() {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID oldAssigneeId = UUID.randomUUID();

        User oldAssignee = createUser(oldAssigneeId);
        User currentUser = createUser(currentUserId);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(oldAssignee)
                .status(TicketStatus.OPEN)
                .createdBy(currentUser)
                .build();
        RequestTicketDto requestTicketDto = new RequestTicketDto("Title", "Description", TicketStatus.IN_PROGRESS,
                TicketPriority.LOW, null);

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);

        // ACT ve ASSERT
        ticketService.updateTicket(ticketId, requestTicketDto);

        // ASSERT
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketMapper, times(1)).toDto(ticket);

    }

    @DisplayName("Bilet güncelleme metodunda güncellenecek olan ticketin bulunması durumunda ve ticket güncellemek isteyen kişinin bileti oluşturan kullanıcı olması durumunda ve  yeni atanacak kullanıcının olması ve null gelmemesi ve kaydının bulunması durumunda ve mevcut bilet durumunun statüsü 'DONE' türünde olması durumnda kayıt altına alınması")
    @Test
    void updateTicket_WhenTicketDone_ShouldReopen() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID oldAssigneeId = UUID.randomUUID();
        UUID newAssigneeId = UUID.randomUUID();

        User newAssignee = createUser(newAssigneeId);
        User oldAssignee = createUser(oldAssigneeId);
        User currentUser = createUser(currentUserId);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(oldAssignee)
                .status(TicketStatus.DONE)
                .createdBy(currentUser)
                .build();
        RequestTicketDto requestTicketDto = new RequestTicketDto("Title", "Description", null,
                TicketPriority.LOW, newAssigneeId);

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.getUserById(requestTicketDto.assignedToId())).willReturn(Optional.of(newAssignee));

        // ACT
        ticketService.updateTicket(ticketId, requestTicketDto);

        // ASSERT
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketMapper, times(1)).toDto(ticket);

    }

    @DisplayName("Bilet güncelleme metodunda güncellenecek olan ticketin bulunması durumunda ve ticket güncellemek isteyen kişinin ticketi oluşturan kullanıcı olması durumunda ve  yeni atanacak kullanıcının olması ve null gelmemesi ve kaydının bulunası durumunda ve request bilet durumunun statüsünün null olmaması ve mevcut bilet durumu ile request durumunun aynı olmaması durumunda ve geçiş kontrollerinin geçerli olması durumunda kayıt altına alınması")
    @Test
    void updateTicket_WithValidStatus_ShouldSucceed() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID oldAssigneeId = UUID.randomUUID();
        UUID newAssigneeId = UUID.randomUUID();

        User newAssignee = createUser(newAssigneeId);
        User oldAssignee = createUser(oldAssigneeId);
        User currentUser = createUser(currentUserId);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(oldAssignee)
                .status(TicketStatus.IN_PROGRESS)
                .createdBy(currentUser)
                .build();
        RequestTicketDto requestTicketDto = new RequestTicketDto("Title", "Description", TicketStatus.DONE,
                TicketPriority.LOW, newAssigneeId);

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.getUserById(requestTicketDto.assignedToId())).willReturn(Optional.of(newAssignee));

        // ACT
        ticketService.updateTicket(ticketId, requestTicketDto);

        // ASSERT
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketMapper, times(1)).toDto(ticket);

    }

    @DisplayName("Bilet güncelleme metodunda bussiness logic olarak güncellenecek olan ticketin bulunması durumunda ve ticket güncellemek isteyen kişinin ticketi oluşturan kullanıcı olması durumunda ve  yeni atanacak kullanıcının olması ve null gelmemesi ve kaydının bulunası durumunda ve request bilet durumunun statüsünün null olmaması ve mevcut bilet durumu ile request durumunun aynı olmaması durumunda ve geçiş kontrollerinin geçerli olmaması durumunda BusinessException türünde hata fırlatılması")
    @Test
    void updateTicket_WithInvalidStatus_ThrowsException() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID oldAssigneeId = UUID.randomUUID();
        UUID newAssigneeId = UUID.randomUUID();

        User newAssignee = createUser(newAssigneeId);
        User oldAssignee = createUser(oldAssigneeId);
        User currentUser = createUser(currentUserId);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(oldAssignee)
                .status(TicketStatus.OPEN)
                .createdBy(currentUser)
                .build();
        RequestTicketDto requestTicketDto = new RequestTicketDto("Title", "Description", TicketStatus.DONE,
                TicketPriority.LOW, newAssigneeId);

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.getUserById(requestTicketDto.assignedToId())).willReturn(Optional.of(newAssignee));

        // ACT
        assertThrows(BusinessException.class, () -> {
            ticketService.updateTicket(ticketId, requestTicketDto);
        });

        // ASSERT
        verify(ticketRepository, never()).save(ticket);
        verifyNoInteractions(ticketMapper);

    }

    @DisplayName("Bilet güncelleme metodunda bussiness logic olarak güncellenecek olan ticketin bulunması durumunda ve ticket güncellemek isteyen kişinin ticketi oluşturan kullanıcı olması durumunda ve  yeni atanacak kullanıcının olması ve null gelmemesi ve kaydının bulunası durumunda ve request bilet durumunun statüsünün null olmaması ve mevcut bilet durumu ile request durumunun aynı olması durumunda kayıt altına alınması")
    @Test
    void updateTicket_WithSameStatus_ShouldSucceed() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        UUID oldAssigneeId = UUID.randomUUID();
        UUID newAssigneeId = UUID.randomUUID();

        User newAssignee = createUser(newAssigneeId);
        User oldAssignee = createUser(oldAssigneeId);
        User currentUser = createUser(currentUserId);

        Ticket ticket = Ticket.builder()
                .id(ticketId)
                .assignedTo(oldAssignee)
                .status(TicketStatus.IN_PROGRESS)
                .createdBy(currentUser)
                .build();
        RequestTicketDto requestTicketDto = new RequestTicketDto("Title", "Description", TicketStatus.IN_PROGRESS,
                TicketPriority.LOW, newAssigneeId);

        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);
        given(userService.getUserById(requestTicketDto.assignedToId())).willReturn(Optional.of(newAssignee));

        // ACT
        ticketService.updateTicket(ticketId, requestTicketDto);

        // ASSERT
        verify(ticketRepository, times(1)).save(ticket);
        verify(ticketMapper, times(1)).toDto(ticket);

    }

    @DisplayName("Bilet id ile arandığında mevcut bilet başarıyla dönmelidir")
    @Test
    void getTicketById_WhenTicketExists_ShouldReturnTicket() {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        Ticket ticket = Ticket.builder().id(ticketId).title("Test Ticket").build();
        given(ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));

        // ACT
        Optional<Ticket> result = ticketService.getTicketById(ticketId);

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("Test Ticket", result.get().getTitle());
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @DisplayName("Bilet id ile arandığında bilet bulunamazsa boş Optional dönmelidir")
    @Test
    void getTicketById_WhenTicketDoesNotExist_ShouldReturnEmptyOptional() {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        given(ticketRepository.findById(ticketId)).willReturn(Optional.empty());

        // ACT
        Optional<Ticket> result = ticketService.getTicketById(ticketId);

        // ASSERT
        assertTrue(result.isEmpty());
        verify(ticketRepository, times(1)).findById(ticketId);
    }

    @DisplayName("Repositoryde bulun(may)an toplam bilet sayısını başarıyla dönmelidir")
    @Test
    void totalTicketCount() {

        // ARRANGE
        long expectedCount = 10L;
        given(ticketRepository.count()).willReturn(expectedCount);

        // ACT
        Long result = ticketService.totalTicketCount();

        // ASSERT
        assertEquals(expectedCount, result);
        verify(ticketRepository, times(1)).count();

    }

    @DisplayName("Tüm statüler için bilet sayıları (boş olanlar dahil 0 olarak) doğru dönmelidir")
    @Test
    void getEachStatusTotalTicketsCount_ShouldReturnMapWithAllStatuses() {
        // ARRANGE
        List<Object[]> mockResults = List.of(
                new Object[] { TicketStatus.OPEN, 5L },
                new Object[] { TicketStatus.IN_PROGRESS, 2L });
        given(ticketRepository.countTicketsByStatusRaw()).willReturn(mockResults);

        // ACT
        Map<TicketStatus, Long> result = ticketService.getEachStatusTotalTicketsCount();

        // ASSERT
        assertEquals(5L, result.get(TicketStatus.OPEN));
        assertEquals(2L, result.get(TicketStatus.IN_PROGRESS));
        assertEquals(0L, result.get(TicketStatus.DONE)); // Veritabanında yok ama Map'te 0 olmalı
        assertEquals(TicketStatus.values().length, result.size());
    }

    @Test
    void getLast5Tickets_WhenListEmpty() {

        List<Ticket> list = List.of();
        given(ticketRepository.findTop5ByOrderByCreatedAtDateDesc()).willReturn(list);

        ticketService.getLast5Tickets();

        verify(ticketMapper, times(0)).toDto(any(Ticket.class));
    }

    @DisplayName("Son oluşturulan 5 bilet oluşturulma tarihine göre listelenmelidir.")
    @Test
    void getLast5Tickets() {

        User user = createUser(UUID.randomUUID());
        Ticket ticket = Ticket.builder()
                .title("title")
                .description("description")
                .priority(TicketPriority.LOW)
                .assignedTo(user)
                .build();
        List<Ticket> list = List.of(ticket);
        given(ticketRepository.findTop5ByOrderByCreatedAtDateDesc()).willReturn(list);

        ticketService.getLast5Tickets();

        verify(ticketMapper, times(1)).toDto(any(Ticket.class));
    }

    @Test
    void filterTickets_WithValidCriteria_ShouldReturnPagedTickets() {
        // ARRANGE
        String title = "title";
        TicketStatus status = TicketStatus.OPEN;
        TicketPriority priority = TicketPriority.MEDIUM;
        UUID assignedToId = UUID.randomUUID();
        int page = 0;
        int size = 10;

        Ticket ticket = Ticket.builder().title("Filtrelenmiş Bilet").build();
        List<Ticket> ticketList = List.of(ticket);
        Page<Ticket> ticketPage = new PageImpl<>(ticketList); 

        given(ticketRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(ticketPage);

        // ACT
        Page<ResponseTicketDto> result = ticketService.filterTickets(title,status, priority, assignedToId, page, size);

        // ASSERT
        assertEquals(1, result.getTotalElements());
        assertEquals("Filtrelenmiş Bilet", result.getContent().get(0).title());
        verify(ticketRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    private Ticket createTicket(User user, RequestCreateTicketDto requestCreateTicketDto) {
        return Ticket.builder()
                .title(requestCreateTicketDto.title())
                .description(requestCreateTicketDto.description())
                .priority(requestCreateTicketDto.priority())
                .assignedTo(user)
                .build();
    }

    private User createUser(UUID userId) {
        return User.builder()
                .id(userId)
                .name("Kafein")
                .surname("Solutions")
                .email("kafein@hotmail.com")
                .password("HashedPassword123!")
                .build();
    }

}
