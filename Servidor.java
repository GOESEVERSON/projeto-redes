import java.io.BufferedReader;			// serve pra ler uma InputStreamReader, quando faz uma conexao via socket, le um arquivo etc.. 
import java.io.File;					//contém métodos úteis para manipular arquivos
import java.io.FileInputStream;			//responsavel por ler entrada de bytes 
import java.io.IOException;				//exceçoes quando ocorrerem error
import java.io.InputStreamReader;		//serve para entrar com dados
import java.io.OutputStream;			
import java.net.ServerSocket;			//ja cria um socket aguardando conexao
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;				
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Servidor {

    public static void main(String[] args) throws IOException {
		// cria um socket "servidor" associado a porta 1234 aguardando conexões
         ServerSocket servidor = new ServerSocket(1234);
         
		 System.out.println("\n\t\t Servidor conectado! Aguardando resposta do cliente(navegador).\n\n");
		 
		 Socket socket = servidor.accept();				//aceita a primeita conexao que tiver
			
        if (socket.isConnected()) {						//verifica se esta conectado
			System.out.println("Computador IPV6 = " + socket.getInetAddress()+ "se conectou ao servidor");		//se conectado imprime na tela o IP do cliente
            BufferedReader buffer = new BufferedReader(new InputStreamReader(socket.getInputStream()));	//cria um BufferedReader a partir do InputStream do cliente
			
			System.out.println("Requisicao: ");
            String linha = buffer.readLine();			//le a primeira linha que contem as informaçoes da requisição
            String[] dadosReq = linha.split(" ");		//quebra a string pelo espaço em branco
            String metodo = dadosReq[0]; 				//pega o metodo
            String caminhoArquivo = dadosReq[1];		//pega o caminho do arquivo
            String protocolo = dadosReq[2];				//pega o protocolo
             
			 while (!linha.isEmpty()) {					//Enquanto a linha não for vazia imprime linha
                System.out.println(linha);			//imprime a linha
                linha = buffer.readLine();			//le a proxima linha
            }
							//
			
            if (caminhoArquivo.equals("/index.html")) {		//se o caminho foi igual a /index.html entao deve pegar o /index.html
                caminhoArquivo = "index.html";
            }
            
            File arquivo = new File(caminhoArquivo.replaceFirst("/index.html", ""));			//abre o arquivo pelo caminho especificado
			String status = protocolo + " 200 OK\r\n";
            
            if (!arquivo.exists()) {					//se o arquivo não existe mostra o erro 404
                status = protocolo + " 404 Not Found\r\n";
                arquivo = new File("404.html");
            }

            
            byte[] conteudo = Files.readAllBytes(arquivo.toPath());		//le todo o conteúdo do arquivo para bytes
						
						//cria o outputStream para enviar a resposta ao servidor, cria-se uma string para a estrutura da resposta 
			
            SimpleDateFormat formatador = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss", Locale.ENGLISH);		//cria um formato para o GMT espeficicado pelo HTTP
            formatador.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date data = new Date();
            
            String dataFormatada = formatador.format(data) + " GMT";//Formata a data para o padrao 
            
            String header = status									//cabeçalho padrão da resposta HTTP
                    + "Location: http://localhost:1234/ \r\n"
                    + "Date: " + dataFormatada + "\r\n" 
                    + "Server: MeuServidor/1.0\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: " + conteudo.length + "\r\n"
                    + "Connection: close\r\n"
                    + "\r\n";
					
			OutputStream resposta = socket.getOutputStream();			//cria o canal de resposta utilizando o outputStream
			resposta.write(header.getBytes());							//escreve o headers em bytes
			resposta.write(conteudo); 					//escreve o conteudo em bytes
            resposta.flush();					 //encerra a resposta
			
			System.out.println("\n\t\t Servidor encerrado!\n");
        }
    }
}
