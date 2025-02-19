
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
            // Criando o objeto URL
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0"); 
            conn.connect();

            // Lê o conteúdo HTML da URL
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            StringBuilder content = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            reader.close();

            // Processa o HTML e extrai o texto do nível mais profundo
            String result = extractDeepestText(content.toString());
            System.out.println(result);

        } catch (IOException e) { 
            System.out.println("URL connection error"); // Erro ao tentar conectar à URL
        }
    }

    private static String extractDeepestText(String htmlContent) {
        Stack<String> tagStack = new Stack<>();
        int maxDepth = 0;
        int currentDepth = 0;
        String deepestText = null;

        String[] lines = htmlContent.split("\n");
        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty()) continue;

            if (line.startsWith("<") && line.endsWith(">")) {
                if (line.startsWith("</")) {
                    if (tagStack.isEmpty() || !tagStack.peek().equals(line.substring(2, line.length() - 1))) {
                        return ""; // Retorna string vazia se houver erro de fechamento de tag
                    }
                    tagStack.pop();
                    currentDepth--;
                } else {
                    String tagName = line.substring(1, line.length() - 1);
                    tagStack.push(tagName);
                    currentDepth++;
                    maxDepth = Math.max(maxDepth, currentDepth);
                }
            } else {
                if (currentDepth == maxDepth && deepestText == null) {
                    deepestText = line;
                }
            }
        }

        return deepestText != null ? deepestText : "";
    }

}



