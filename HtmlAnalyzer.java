
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
/*import java.net.MalformedURLException;*/
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
            // Cria um objeto URL e abre uma conexão HTTP
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); // Evita bloqueios de servidores
            conn.connect();

            // Lê o conteúdo HTML da URL usando BufferedReader
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n"); // Armazena todas as linhas do HTML
            }
            reader.close();

            // Processa o HTML e extrai o texto do nível mais profundo
            String result = extractDeepestText(content.toString());
            System.out.println(result);

       /* } catch (MalformedURLException e) {*/ 
            System.out.println("Erro: A URL fornecida está malformada."); // Erro de URL inválida
        } catch (IOException e) { // catch para lidar com o erro
            System.out.println("URL connection error"); // Erro ao tentar conectar à URL
        }
    }

    private static String extractDeepestText(String htmlContent) {
        Stack<String> tagStack = new Stack<>(); // Pilha para armazenar tags abertas
        int maxDepth = 0; // buscar a profundidade a partir de 0
        int currentDepth = 0; // Profundidade atual na navegação
        String deepestText = null;
        /* boolean malformed = false; */ 

        String[] lines = htmlContent.split("\n"); // para dividir  o HTML em linhas
        for (String line : lines) {
            line = line.trim(); // remove espaços em branco no início e no fim da linha

            if (line.isEmpty()) continue; // Ignora linhas vazias

            if (line.startsWith("<") && line.endsWith(">")) {
                if (line.startsWith("</")) { // Se for uma tag de fechamento
                    if (tagStack.isEmpty() || !tagStack.peek().equals(line.substring(2, line.length() - 1))) {
                        /* malformed = true; */ 
                        break; // se as tags no html não respondem, não é colocado
                    }
                    tagStack.pop(); // Remove a tag da pilha
                    currentDepth--; // Diminui a profundidade
                } else { // Se for uma tag de abertura
                    String tagName = line.substring(1, line.length() - 1); // Extrai o nome da tag
                    tagStack.push(tagName); // coloca  a tag na pilha
                    currentDepth++; // Aumenta a profundidade
                    maxDepth = Math.max(maxDepth, currentDepth); // Atualiza a profundidade máxima
                }
            } else { // Se for um trecho de texto
                if (currentDepth == maxDepth && deepestText == null) {
                    deepestText = line; // guarda o texto encontrado
                }
            }
        }

        /* 
        if (malformed || !tagStack.isEmpty()) {
            return "malformed HTML"; 
        } 
        */

        // Retorna o texto do nível mais profundo encontrado ou uma string vazia se não houver texto
        return deepestText != null ? deepestText : "";
    }
}

