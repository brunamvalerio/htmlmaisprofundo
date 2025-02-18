/*import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.regex.*;
import java.util.*;

public class HTMLExtractor {

    // Método para obter HTML de uma URL
    public static String getHTML(String urlString) throws Exception {
        // Cria o objeto URL
        URL url = new URL(urlString);
        
        // Abre a conexão
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET"); // Método GET
        
        // Lê o conteúdo da resposta
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        
        // Lê linha por linha e concatena
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine).append("\n");
        }
        
        in.close(); // Fecha o reader
        
        return content.toString(); // Retorna o conteúdo HTML
    }

    // Método para extrair o texto mais profundo
    public static String getDeepestText(String html) {
        // Expressão regular para pegar o conteúdo entre as tags
        Pattern pattern = Pattern.compile("<[^>]+>([^<]+)</[^>]+>");
        Matcher matcher = pattern.matcher(html);
        
        List<String> texts = new ArrayList<>();
        
        // Encontrar todas as ocorrências
        while (matcher.find()) {
            String text = matcher.group(1).trim();
            if (!text.isEmpty()) {
                texts.add(text); // Adiciona o texto encontrado
            }
        }

        // Se encontrou textos, retorna o mais profundo (último encontrado)
        if (!texts.isEmpty()) {
            return texts.get(texts.size() - 1);  // Último texto é o mais profundo
        } else {
            return "Nenhum texto encontrado.";
        }
    }

    public static void main(String[] args) {
        try {
            // Substitua pela URL que você deseja analisar
            String url = "https://www.bbc.com/";  // Exemplo de URL
            String htmlContent = getHTML(url);  // Obtém o HTML da URL
            
            String deepestText = getDeepestText(htmlContent);  // Extrai o texto mais profundo
            System.out.println("Texto mais profundo: " + deepestText);  // Exibe o texto encontrado
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
*/