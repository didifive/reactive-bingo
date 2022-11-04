# Desafio do curso Spring WebFlux

Criar uma API de jogo de Bingo usando as seguintes tecnologias:

![technology Java](https://img.shields.io/badge/techonolgy-Java-red)
![technology MongoDB](https://img.shields.io/badge/techonolgy-MongoDB-green)
![techonolgy Spring WebFlux](https://img.shields.io/badge/techonolgy-SpringWebFlux-brightgreen)
![technology Docker](https://img.shields.io/badge/techonolgy-Docker-blue)

## Requisitos
 * Gerenciar as informações dos jogadores (CRUD) com um find on demand;
 * Gerar as cartelas de uma rodada com os números aleatórios, regras:
   * Todas as cartelas geradas devem ter quantidades iguais de números;
   * A cartela deve ter 20 números;
   * Uma cartela pode ter no máximo 1/4 dos mesmos números de uma outra cartela;
   * as cartelas da rodada só podem ser geradas antes de começar o sorteio dos números;
 * Possibilidade de vincular uma cartela ao jogador ( 1 jogador só pode ser vinculado á uma cartela por rodada);
 * Guardar um histórico das rodadas com os números sorteados, regras:
   * Cada rodada pode sortear números de 0 até 99;
   * Guardar os números sorteados;
   * Guardar as cartelas que pertencem a ela;
   * Guardar os jogadores que participaram;
 * Endpoint para sortear o próximo número da rodada (um número não pode ser sorteado 2x na mesma rodada);
 * Endpoint para buscar o último número sorteado;
 * Cada vez que um número é sorteado deve-se verificar se alguma cartela já completou todos os números, caso tenha completado a rodada deve ser encerrada (bloquear geração de novos números) e um e-mail deve ser enviado ao vencedor da partida e os outros jogadores devem receber um e-mail mostrando como eles se sairam;
 * Endpoint para buscar todas as rodadas (find all) o find on demand fica como opcional;
 * Endpoint para buscar informações de uma partida pelo identificador 
 * Dockerizar a aplicação (opcional);
 * Montar documentação dos endpoints (opcional);

## Requisitos de testes
 * Os testes devem contemplar controller, services e repositórios que não são interfaces

 ## Dicas
 Sugestão de endpoints:
 * Jogadores:
   * save (POST /players)
   * update (PUT /players/{id})
   * delete (DELETE /players/{id)
   * find by id (GET /players/{id})
   * find on demand (GET /players)
 * Rodada:
   * criar rodada (POST /rounds)
   * gerar número (POST /rounds/{id}/generate-number)
   * buscar ultimo número sorteado (GET /rounds/{id}/current-number)
   * gerar cartela (POST /rounds/{id}/bingo-card/{playerId})
   * buscar rodadas (GET /rounds)
   * buscar rodada pelo id (GET /rounds/{id})

# Reactive Bingo by Luis Zancanela

As informações acima são para o desafio, abaixo estarão as informações do projeto conforme desenvolvido para cumprir o desafio lançado.

## Configuração

O projeto foi feito utilizando:
* IDE IntelliJ IDEA Community Edition 2022.1.1.
* Iniciado com [Spring Initializr] com as configurações e dependências:
  * Project: Gradle Project
  * Language: Java
  * Spring Boot 2.7.5
  * Packaging: Jar
  * Java 17
  * Dependencies:
    * Spring Data Reactive MongoDB
    * Embedded MongoDB Database (para teste)
    * Spring Reactive Web
    * Java Mail Sender
    * Validation
    * Lombok

## Visuais
Logotipo do projeto:
![Reactive Bingo Logo](docs/logo.png?raw=true "Reactive Bingo Logo")  
Banner do Spring personalizado: 
![Spring Banner personalizado](docs/banner-springboot.png?raw=true "Spring Banner personalizado")  
Diagrama de Classes UML:
![UML Class Diagram](docs/uml-diagram.drawio.png?raw=true "UML Class Diagram")  


[Spring Initializr]: https://start.spring.io/