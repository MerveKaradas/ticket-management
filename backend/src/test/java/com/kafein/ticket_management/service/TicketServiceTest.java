package com.kafein.ticket_management.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;

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
import com.kafein.ticket_management.model.enums.TicketPriority;
import com.kafein.ticket_management.model.enums.TicketStatus;
import com.kafein.ticket_management.repository.TicketRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.*;

import static com.kafein.ticket_management.util.TestDataFactory.createTestUser;
import static com.kafein.ticket_management.util.TestDataFactory.createSystemPoolUser;
import static com.kafein.ticket_management.util.TestDataFactory.createTestTicket;

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

    @Captor
    private ArgumentCaptor<Ticket> ticketArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<Ticket>> ticketListCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    @Test
    void createTicket_WhenUserExists_ShouldReturnResponse() {

        // ARRANGE
        User user = createTestUser();
        RequestCreateTicketDto requestCreateTicketDto = new RequestCreateTicketDto("Title", "Description",
                TicketPriority.LOW, user.getId());

        Ticket ticket = createTestTicket(user, requestCreateTicketDto);

        given(userService.getUserById(user.getId())).willReturn(Optional.of(user));
        given(ticketRepository.save(any(Ticket.class))).willReturn(ticket);

        // ACT
        ResponseCreateTicketDto result = ticketService.createTicket(requestCreateTicketDto);

        // ASSERT
        verify(ticketRepository, times(1)).save(ticketArgumentCaptor.capture());
        Ticket capturedTicket = ticketArgumentCaptor.getValue();

        assertThat(capturedTicket)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(ticket);

        assertThat(capturedTicket.getStatus()).isEqualTo(TicketStatus.OPEN);

        verifyNoMoreInteractions(ticketRepository);

    }

    @Test
    void createTicket_WhenUserDoesNotExist_ThrowsException() {

        // ARRANGE
        UUID userId = UUID.randomUUID();
        RequestCreateTicketDto requestDto = new RequestCreateTicketDto("Title", "Description", TicketPriority.LOW,
                userId);

        given(userService.getUserById(userId)).willReturn(Optional.empty());

        // ACT ve ASSERT
        assertThatThrownBy(() -> {
            ticketService.createTicket(requestDto);
        })
                .isInstanceOf(ResourceNotFoundException.class)
                .hasNoCause(); 

        verify(ticketRepository, never()).save(any(Ticket.class));
        // Mapper'a da hiç gidilmemeli
        verifyNoInteractions(ticketMapper);

    }

    // INFO : Yetki kontrolü için spring'in ayakta olması gerekir, slice testte kontrol sağlanacak
    @Test
    void deleteTicket_AsAdmin_ShouldSucceed() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();

        given(ticketRepository.existsById(ticketId)).willReturn(true);

        // ACT
        ticketService.deleteTicketById(ticketId);

        // ASSERT
        verify(ticketRepository, times(1)).deleteById(ticketId);
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void deleteTicket_WhenTicketNotFound_ThrowsException() {

        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        given(ticketRepository.existsById(ticketId)).willReturn(false);

        // ACT ve ASSERT
        assertThatThrownBy(() -> {
            ticketService.deleteTicketById(ticketId);
        })
                .isInstanceOf(ResourceNotFoundException.class)
                .hasNoCause();

        verify(ticketRepository, never()).deleteById(ticketId);
        verifyNoMoreInteractions(ticketRepository);

    }

    // INFO : Yetki kontrolü için spring'in ayakta olması gerekir, slice testte kontrol sağlanacak
    @Test
    void deleteAllTickets_AsAdmin_ShouldSucceed() {

        // ACT
        ticketService.deleteAllTickets();

        // ASSERT
        verify(ticketRepository, times(1)).deleteAll();
        verifyNoMoreInteractions(ticketRepository);

    }

    // INFO : Yetki kontrolü için spring'in ayakta olması gerekir slice testte kontrol sağlanacak
    @Test
    void getAllTickets_AsAdmin_ShouldReturnList() {

        // ARRANGE
        User user = createTestUser();
        Ticket ticket1 = createTestTicket(user);
        Ticket ticket2 = createTestTicket(user);
        List<Ticket> ticketList = List.of(ticket1, ticket2);
        given(ticketRepository.findAll()).willReturn(ticketList);

        // ACT
        var result = ticketService.getAllTickets();

        // ASSERT
        assertThat(result)
                .as("Dönen bilet listesi boş olmamalı ve beklenen sayıda bilet içermeli")
                .isNotEmpty()
                .hasSize(2);

        verify(ticketRepository, times(1)).findAll();
        verify(ticketMapper, times(2)).toDto(any(Ticket.class)); 
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void updateStatus_WhenUserNotAssigned_ThrowsException() {
        // ARRANGE
        User assignee = createTestUser();
        User currentUser = createTestUser();
        TicketStatus status = TicketStatus.IN_PROGRESS;
        TicketStatusUpdateRequestDto dtoStatus = new TicketStatusUpdateRequestDto(status);

        Ticket ticket = createTestTicket(assignee, status);

        given(ticketRepository.findById(ticket.getId())).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(currentUser);

        // ACT ve ASSERT
        assertThrows(AccessDeniedException.class, () -> {
            ticketService.updateTicketStatus(ticket.getId(), dtoStatus);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepository);

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
        User user = createTestUser();
        TicketStatusUpdateRequestDto requestStatusDto = new TicketStatusUpdateRequestDto(targetStatus);
        Ticket ticket = createTestTicket(user, initialStatus);

        given(ticketRepository.findById(ticket.getId())).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(user);

        // ACT
        ticketService.updateTicketStatus(ticket.getId(), requestStatusDto);

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
        User user = createTestUser();
        TicketStatusUpdateRequestDto requestStatusDto = new TicketStatusUpdateRequestDto(targetStatus);
        Ticket ticket = createTestTicket(user, initialStatus);

        given(ticketRepository.findById(ticket.getId())).willReturn(Optional.of(ticket));
        given(userService.getCurrentUser()).willReturn(user);

        // ACT ve ASSERT
        assertThrows(BusinessException.class, () -> {
            ticketService.updateTicketStatus(ticket.getId(), requestStatusDto);
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

        User otherUser = createTestUser();
        User currentUser = createTestUser();

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
        UUID newAssigneeId = UUID.randomUUID();

        User oldAssignee = createTestUser();
        User currentUser = createTestUser();

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

        User oldAssignee = createTestUser();
        User currentUser = createTestUser();

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
        UUID newAssigneeId = UUID.randomUUID();

        User newAssignee = createTestUser();
        User oldAssignee = createTestUser();
        User currentUser = createTestUser();

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
        UUID newAssigneeId = UUID.randomUUID();

        User newAssignee = createTestUser();
        User oldAssignee = createTestUser();
        User currentUser = createTestUser();

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
        UUID newAssigneeId = UUID.randomUUID();

        User newAssignee = createTestUser();
        User oldAssignee = createTestUser();
        User currentUser = createTestUser();

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
        UUID newAssigneeId = UUID.randomUUID();

        User newAssignee = createTestUser();
        User oldAssignee = createTestUser();
        User currentUser = createTestUser();

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

    @Test
    void updateAllTicketsByDeletedUserId_ShouldSucceed() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        User systemPool = createSystemPoolUser();
        List<Ticket> activeTickets = List.of(Ticket.builder().build(), new Ticket());

        given(userService.getSystemPool()).willReturn(systemPool);
        given(ticketRepository.findAllByassignedTo_IdAndStatusNot(userId, TicketStatus.DONE)).willReturn(activeTickets);

        // ACT
        ticketService.updateAllTicketsByDeletedUserId(userId);

        // ASSERT
        verify(ticketRepository, times(1)).saveAll(ticketListCaptor.capture());

        List<Ticket> capturedTickets = ticketListCaptor.getValue();

        assertThat(capturedTickets).hasSize(2);
        assertThat(capturedTickets).allSatisfy(ticket -> {
            assertThat(ticket.getTitle()).isEqualTo("Atama bekliyor!");
            assertThat(ticket.getStatus()).isEqualTo(TicketStatus.BACKLOG);
            assertThat(ticket.getAssignedTo()).isEqualTo(systemPool);
        });

        verifyNoMoreInteractions(ticketRepository);

    }

    @Test
    void updateAllTicketsByDeletedUserId_WhenEmptyList() {
        // ARRANGE
        UUID userId = UUID.randomUUID();
        given(ticketRepository.findAllByassignedTo_IdAndStatusNot(userId, TicketStatus.DONE))
                .willReturn(Collections.emptyList());

        // ACT
        ticketService.updateAllTicketsByDeletedUserId(userId);

        // ASSERT
        verify(ticketRepository, never()).saveAll(anyList());
        verifyNoMoreInteractions(ticketRepository);

    }

    @Test
    void claimTicket_ShouldSucceed() {
        // ARRANGE
        User systemPool = createSystemPoolUser();
        Ticket ticket = Ticket.builder().assignedTo(systemPool).build();
        RequestTicketClaimDto dto = new RequestTicketClaimDto("Yeni baslik");

        given(ticketRepository.findById(ticket.getId())).willReturn(Optional.of(ticket));

        // ACT
        ticketService.claimTicket(ticket.getId(), dto);

        // ASSERT
        verify(ticketRepository, times(1)).save(ticketArgumentCaptor.capture());
        verifyNoMoreInteractions(ticketRepository);

        Ticket capturedTicket = ticketArgumentCaptor.getValue();
        assertThat(capturedTicket.getStatus()).isEqualTo(TicketStatus.IN_PROGRESS);
        assertThat(capturedTicket.getTitle()).isEqualTo(dto.newTitle());

    }

    @Test
    void claimTicket_WhenTicketDoesNotExist_ThrowsException() {
        // ARRANGE
        UUID ticketId = UUID.randomUUID();
        RequestTicketClaimDto dto = new RequestTicketClaimDto("Yeni baslik");

        given(ticketRepository.findById(ticketId)).willReturn(Optional.empty());

        // ACT
        assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.claimTicket(ticketId, dto);
        });

        // ASSERT
        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(ticketMapper, never()).toDto(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepository);

    }

    @Test
    void claimTicket_WhenTicketAlreadyClaim_ThrowsException() {
        // ARRANGE
        User user = createTestUser(UUID.randomUUID());
        Ticket ticket = Ticket.builder().assignedTo(user).build();
        RequestTicketClaimDto dto = new RequestTicketClaimDto("Yeni baslik");

        given(ticketRepository.findById(ticket.getId())).willReturn(Optional.of(ticket));

        // ACT
        assertThrows(AccessDeniedException.class, () -> {
            ticketService.claimTicket(ticket.getId(), dto);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(ticketMapper, never()).toDto(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepository);

    }

    @Test
    void getTicket_WhenTicketExists_ShouldSucceed() {
        // ARRANGE
        User user = createTestUser();
        Ticket ticket = createTestTicket(user);

        given(ticketRepository.findById(ticket.getId())).willReturn(Optional.of(ticket));

        // ACT
        ResponseTicketDto result = ticketService.getTicket(ticket.getId());

        verify(ticketRepository, times(1)).findById(ticket.getId());
        verify(ticketMapper, times(1)).toDto(ticket);
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void getTicket_WhenTicketDoesNotExist_ThrowsResourceNotFoundException() {
        UUID ticketId = UUID.randomUUID();

        given(ticketRepository.findById(ticketId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            ticketService.getTicket(ticketId);

        });

        verify(ticketRepository, never()).save(any(Ticket.class));
        verify(ticketMapper, never()).toDto(any(Ticket.class));
        verifyNoMoreInteractions(ticketRepository);
    }

    @Test
    void filter() {
        String title = "";
        TicketStatus status = TicketStatus.IN_PROGRESS;
        TicketPriority priority = TicketPriority.LOW;
        UUID assignedToId = UUID.randomUUID();
        int page = 1;
        int size = 20;

        // Mockito ile Page objesi dönmek için PageImpl kullanılır
        Page<Ticket> mockPage = new PageImpl<>(List.of(new Ticket()), PageRequest.of(page, size), 1);

        given(ticketRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(mockPage);

        // ACT 
        ticketService.filterTickets(title, status, priority,
                assignedToId, page, size);

        // ASSERT
        verify(ticketRepository).findAll(any(Specification.class), pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(page);
        assertThat(capturedPageable.getPageSize()).isEqualTo(size);

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

        given(ticketRepository.findAll(any(Specification.class),
                any(Pageable.class))).willReturn(ticketPage);

        // ACT
        Page<ResponseTicketDto> result = ticketService.filterTickets(title, status,
                priority, assignedToId, page, size);

        // ASSERT
        assertEquals(1, result.getTotalElements());
        assertEquals("Filtrelenmiş Bilet", result.getContent().get(0).title());
        verify(ticketRepository, times(1)).findAll(any(Specification.class),
                any(Pageable.class));
    }

}
