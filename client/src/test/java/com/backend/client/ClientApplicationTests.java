package com.backend.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.backend.client.model.Client;
import com.backend.client.model.dto.ClientDto;
import com.backend.client.repository.ClientRepository;
import com.backend.client.service.ClientServiceImpl;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ClientApplicationTests {

	@Mock
	private ClientRepository clientRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private ClientServiceImpl clientService;

	@BeforeEach
	void setUp() {
		when(clientRepository.existsByDni(anyString())).thenReturn(Mono.just(false));
		when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> "encoded-" + invocation.getArgument(0));
		when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
			Client client = invocation.getArgument(0);
			client.setId(1L);
			return Mono.just(client);
		});
	}

	@Test
	void createClient() {
		ClientDto request = new ClientDto(null, "1712345678", "Jose Lema", "1234", "Masculino", 30,
				"Otavalo sn y principal", "098254785", true);

		ClientDto created = clientService.create(request).block();

		assertThat(created).isNotNull();
		assertThat(created.getId()).isEqualTo(1L);
		assertThat(created.getName()).isEqualTo("Jose Lema");
		assertThat(created.getPassword()).isEqualTo("encoded-1234");

		verify(clientRepository).existsByDni("1712345678");
		verify(clientRepository).save(any(Client.class));
	}

}
