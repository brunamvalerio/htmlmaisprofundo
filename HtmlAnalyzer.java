import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Stack;

public class HtmlAnalyzer {
    public static void main(String[] args) {
        // Verifica se foi passada uma URL como argumento
        if (args.length < 1) {
            System.out.println("Por favor, forneça uma URL.");
            return;
        }

        String urlString = args[0];

        try {
            // Cria um objeto URL a partir da string fornecida
            URL url = new URL(urlString);

            // Estabelece a conexão HTTP com a URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");  // Método de requisição GET
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");  // Define um cabeçalho de agente de usuário para a requisição
            conn.connect();  // Estabelece a conexão

            // Lê o conteúdo da resposta HTTP
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder content = new StringBuilder();  // Usado para armazenar o conteúdo HTML
            String line;

            // Lê linha por linha do conteúdo e armazena no StringBuilder
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();  // Fecha o BufferedReader após terminar a leitura

            // Verifica se o HTML é válido
            if (isValidHtml(content.toString())) {
                // Se o HTML for válido, extrai o texto do nível mais profundo
                String result = extractDeepestText(content.toString());
                System.out.println(result);  // Exibe o resultado
            } else {
                // Se o HTML for malformado, exibe a mensagem correspondente
                System.out.println("malformed HTML");
            }

        } catch (IOException e) {
            // Se ocorrer um erro na conexão com a URL, exibe a mensagem de erro
            System.out.println("URL connection error");
        }
    }

    // Função para verificar se o HTML é bem formado
    public static boolean isValidHtml(String htmlContent) {
        Stack<String> tagStack = new Stack<>();  // Pilha usada para verificar o balanceamento das tags

        boolean insideTag = false;  // Indica se está dentro de uma tag HTML
        StringBuilder tagBuffer = new StringBuilder();  // Buffer para armazenar o nome das tags

        // Percorre todo o conteúdo HTML
        for (int i = 0; i < htmlContent.length(); i++) {
            char c = htmlContent.charAt(i);

            if (c == '<') {  // Início de uma tag
                insideTag = true;  // Está dentro de uma tag
                tagBuffer.setLength(0);  // Limpa o buffer da tag
            } else if (c == '>') {  // Fim de uma tag
                insideTag = false;  // Não está mais dentro de uma tag
                String tag = tagBuffer.toString().trim();  // Recupera o nome da tag
                tagBuffer.setLength(0);  // Limpa o buffer

                if (tag.startsWith("/")) {  // Se for uma tag de fechamento
                    // Verifica se a tag de fechamento corresponde à tag de abertura
                    if (tagStack.isEmpty() || !tagStack.peek().equals(tag.substring(1))) {
                        return false;  // HTML malformado
                    }
                    tagStack.pop();  // Remove a tag de abertura correspondente
                } else {  // Se for uma tag de abertura
                    tagStack.push(tag);  // Adiciona a tag de abertura na pilha
                }
            } else if (insideTag) {  // Se estiver dentro de uma tag
                tagBuffer.append(c);  // Adiciona o caractere ao buffer da tag
            }
        }

        // Se a pilha estiver vazia, significa que todas as tags de abertura foram fechadas corretamente
        return tagStack.isEmpty();
    }

    // Função para extrair o texto do nível mais profundo do HTML
    public static String extractDeepestText(String htmlContent) {
        Stack<String> tagStack = new Stack<>();  // Pilha usada para controlar o nível de profundidade
        int maxDepth = 0;  // A profundidade máxima encontrada
        int currentDepth = 0;  // A profundidade atual enquanto percorre as tags
        String deepestText = "";  // O texto do nível mais profundo
        StringBuilder textBuffer = new StringBuilder();  // Buffer para armazenar o texto

        boolean insideTag = false;  // Indica se está dentro de uma tag
        boolean insideText = false;  // Indica se está dentro do texto entre tags

        // Percorre todo o conteúdo HTML
        for (int i = 0; i < htmlContent.length(); i++) {
            char c = htmlContent.charAt(i);

            if (c == '<') {  // Início de uma tag
                // Se estiver dentro de texto e a profundidade atual for maior ou igual à profundidade máxima
                if (insideText && currentDepth >= maxDepth) {
                    String text = textBuffer.toString().trim();  // Recupera o texto
                    if (!text.isEmpty()) {
                        // Se a profundidade atual for maior que a máxima, atualiza a profundidade máxima e o texto
                        if (currentDepth > maxDepth) {
                            maxDepth = currentDepth;
                            deepestText = text;
                        }
                    }
                }
                textBuffer.setLength(0);  // Limpa o buffer de texto
                insideTag = true;  // Está dentro de uma tag
                insideText = false;  // Não está mais dentro de texto
            } else if (c == '>') {  // Fim de uma tag
                insideTag = false;  // Não está mais dentro de uma tag
                String tag = textBuffer.toString().trim();  // Recupera o nome da tag
                textBuffer.setLength(0);  // Limpa o buffer

                if (tag.startsWith("/")) {  // Se for uma tag de fechamento
                    if (!tagStack.isEmpty() && tagStack.peek().equals(tag.substring(1))) {
                        tagStack.pop();  // Remove a tag de abertura correspondente
                        currentDepth--;  // Diminui a profundidade
                    }
                } else {  // Se for uma tag de abertura
                    int spaceIndex = tag.indexOf(" ");  // Verifica se a tag tem atributos
                    String tagName = (spaceIndex == -1) ? tag : tag.substring(0, spaceIndex);  // Recupera o nome da tag
                    tagStack.push(tagName);  // Adiciona a tag de abertura na pilha
                    currentDepth++;  // Aumenta a profundidade
                }
            } else {
                // Se não estiver dentro de uma tag, está dentro do texto
                if (insideTag) {
                    textBuffer.append(c);  // Adiciona ao buffer da tag
                } else {
                    insideText = true;  // Está dentro de texto
                    textBuffer.append(c);  // Adiciona ao buffer de texto
                }
            }
        }

        // Finaliza caso tenha algum texto no nível mais profundo
        if (insideText && currentDepth >= maxDepth) {
            String text = textBuffer.toString().trim();
            if (!text.isEmpty()) {
                if (currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                    deepestText = text;
                }
            }
        }

        return deepestText;  // Retorna o texto do nível mais profundo
    }
}

