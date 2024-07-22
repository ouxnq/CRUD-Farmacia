package com.generation.crud_farmacia.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.crud_farmacia.model.Produto;
import com.generation.crud_farmacia.model.Usuario;
import com.generation.crud_farmacia.repository.UsuarioRepository;
import com.generation.crud_farmacia.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProdutoControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();

		usuarioService.cadastrarUsuario(new Usuario(0L, "rootroot", "rootroot"));
	}
	
	@Test
	@DisplayName("Criar Um Produto")
	public void deveCriarUmProduto() {

		HttpEntity<Produto> corpoRequisicao = new HttpEntity<Produto>(
				new Produto(0L, "Dipirona", 15, 14));

		ResponseEntity<Produto> corpoResposta = testRestTemplate.withBasicAuth("rootroot", "rootroot").exchange("/produtos/teste", HttpMethod.POST,
				corpoRequisicao, Produto.class);

		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	@Test
    @DisplayName("Atualizar um Produto")
    public void deveAtualizarUmProduto() {
		
        HttpEntity<Produto> produtoInicial = new HttpEntity<Produto>(
				new Produto(0L, "Dipirona", 15, 14));
        
        ResponseEntity<Produto>respostaCriacao = testRestTemplate.withBasicAuth("rootroot", "rootroot").exchange("/produtos/teste", HttpMethod.POST,
				produtoInicial, Produto.class);

        assertEquals(HttpStatus.CREATED, respostaCriacao.getStatusCode());
        
        
        Produto produtoUpdate = new Produto(respostaCriacao.getBody().getId(), "Dipirona Mono Hidratada", 12,30);
        HttpEntity<Produto> corpoRequisicao = new HttpEntity<>(produtoUpdate);
        

        ResponseEntity<Produto> corpoResposta = testRestTemplate
                .withBasicAuth("rootroot", "rootroot")
                .exchange("/produtos/teste", HttpMethod.PUT, corpoRequisicao, Produto.class);

        assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
    }
	
	@Test
	@DisplayName("Deletar um Produto")
	public void deveDeletarUmProduto() {
	    HttpEntity<Produto> produtoInicial = new HttpEntity<>(new Produto(0L, "Dipirona", 15, 14));
	    ResponseEntity<Produto> respostaCriacao = testRestTemplate.withBasicAuth("rootroot", "rootroot")
	            .exchange("/produtos/teste", HttpMethod.POST, produtoInicial, Produto.class);
	    
	    assertEquals(HttpStatus.CREATED, respostaCriacao.getStatusCode());
	    Long id = respostaCriacao.getBody().getId();

	    ResponseEntity<Void> respostaDelecao = testRestTemplate.withBasicAuth("rootroot", "rootroot")
	            .exchange("/produtos/{id}", HttpMethod.DELETE, null, Void.class, id);
	    
	    assertEquals(HttpStatus.NO_CONTENT, respostaDelecao.getStatusCode());

	    ResponseEntity<Produto> respostaBusca = testRestTemplate.withBasicAuth("rootroot", "rootroot")
	            .exchange("/produtos/{id}", HttpMethod.GET, null, Produto.class, id);
	    
	    assertEquals(HttpStatus.NOT_FOUND, respostaBusca.getStatusCode());
	}
}