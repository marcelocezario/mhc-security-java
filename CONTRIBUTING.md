# mhc-security

## Pacote de segurança Java mhc.dev.br

### Pré requisitos

1. Repositório precisa estar no GitHub (público ou privado);
1. O projeto precisa estar com um `groupId`, `artifactId`, `version` definidos no `pom.xml`.

### Conectando no GitHub

Para gerar um build reutilizável por outras aplicações, é necessário publicar o projeto num repositório e utilizar
GitHub packages, para isso, siga os seguinte passos:

1. Crie um repositório para a aplicação no GitHub;
1. Na conta GitHub do repositório, crie um token de acesso (https://github.com/settings/tokens -
   `Generate new token (classic)`), adicione uma identificação qualquer (`note`), marque as permissões:
   `write:packages`, `read:packages`, `delete:packages`, `repo`, guarde com segurança os dados do token, se necessário
   recuperar essa informação será necessário gerar um novo token;
1. Com o token criado, no seu ambiente local vá até o repositório do Maven, procure pelo arquivo `settings.xml` (
   `~/.m2/settings.xml`):
    ```xml
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">
        <servers>
            <server>
                <id>github</id>
                <username>SEU_USUARIO_GITHUB</username>
                <password>SEU_TOKEN</password>
            </server>
        </servers>
    </settings>
    ```    
1. Adicione a seguinte seção no `pom.xml` do seu projeto que vai ser como dependência para outros:
    ```xml
    <distributionManagement>
        <repository>
            <id>github</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/SEU_USUARIO/NOME_DO_REPOSITORIO</url>
        </repository>
    </distributionManagement>
    ```

### Deploy do package

Estando tudo configurado corretamente, basta executar: ```mvn clean deploy```, o maven vai rodar os testes
automatizados, se não houver nenhuma falha, vai gerar o build e a aplicação será enviada para o GitHub packages, sendo
assim disponibilizada para ser usado como dependência em outras aplicações.

### Testes automatizados

Para executar os testes automátizados, basta executar `mvn clean test`.

#### Cobertura de testes

Para testar a cobertura de testes, execute: `mvn verify`, feito isso var ser gera o arquivo de análise
em: [/target/site/jacoco/index.html](./target/site/jacoco/index.html), caso não tenha sido gerado o arquivo, pode tentar
executar `mvn clean test jacoco:report`.