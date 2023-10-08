package dio.padroesprojeto.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import dio.padroesprojeto.model.Cliente;
import dio.padroesprojeto.model.Endereco;
import dio.padroesprojeto.repository.ClienteRepository;
import dio.padroesprojeto.repository.EnderecoRepository;
import dio.padroesprojeto.service.ClienteService;
import dio.padroesprojeto.service.ViaCepService;

public class ClienteServiceImpl implements ClienteService{
	@Autowired
	private ClienteRepository clienteRepository;
	@Autowired
	private EnderecoRepository enderecoRepository;
	@Autowired
	private ViaCepService viaCepService;
	
	@Override
	public Iterable<Cliente> buscarTodos() {
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		return clienteRepository.findById(id).get();
	}

	@Override
	public void inserir(Cliente cliente) {
		salvandoClientePeloCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		Optional<Cliente> clienteAtualizar = clienteRepository.findById(id);
		// Buscar Cliente por Id, caso exista:
		if(clienteAtualizar.isPresent()) {
			salvandoClientePeloCep(cliente);
		}
	}

	@Override
	public void deletar(Long id) {
		clienteRepository.deleteById(id);
		
	}
	
	private void salvandoClientePeloCep(Cliente cliente) {
		// Verificar se o endereço do cliente já existe (pelo CEP)
		String cep = cliente.getEndereco().getCep();
		Endereco endereco = enderecoRepository.findById(cep).orElseGet(() -> {
			// Caso não exista, ingtegrar com o viaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereço (novo ou existente).
		clienteRepository.save(cliente);
	}

}
