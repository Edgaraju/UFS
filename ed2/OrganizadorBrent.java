package ed2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrganizadorBrent implements FileOrganizer {

    // Tamanho da tabela de registros do arquivo
    private final int P = 11; 
    
    // N�mero de bytes que um registro ocupa
    private final int TAMANHO_REGISTRO = 157;
    
    // Canal de comunica��o com o arquivo
    private FileChannel canal;
    
    public OrganizadorBrent(String path) throws FileNotFoundException{
        File file = new File(path);
        RandomAccessFile rf = new RandomAccessFile(file, "rw");
        this.canal = rf.getChannel();
    }
    
    private int calculaHash(int matricula) {
        return (matricula % this.P);
    }
    
    private int calculaIncremento(int matricula) {
        return ((matricula / this.P) % this.P);
        //return (matricula % (this.P - 2)) + 1;
    }
    
    @Override
    public void addReg(Aluno a) {
        ByteBuffer buf = ByteBuffer.allocate(TAMANHO_REGISTRO);
        
        int matric = a.getMatricula();
        int hash = calculaHash(matric);
        int posicao = hash * TAMANHO_REGISTRO;
        int x;

        try {
            canal.position(posicao);
            canal.read(buf);
            buf.flip();
            x = buf.getInt();
            buf.clear();
            
            System.out.println("{" + posicao + "}  LIDO: " + x);
            // Se a posi��o estiver livre
            if (x == 0 || x == -1) {
                canal.position(posicao);
                canal.write(a.getByteBuffer());
                buf.clear();
            }
            // Houve uma colis�o
            else {
                int colisao = x;
                int incremento = calculaIncremento(matric);
                int passos = 1;
                while(x != 0 && x != -1) {
                    passos++;
                    posicao = (((posicao/TAMANHO_REGISTRO) + incremento) % this.P) * TAMANHO_REGISTRO;
                    System.out.println(posicao);
                    canal.position(posicao);
                    canal.read(buf);
                    buf.flip();
                    x = buf.getInt();
                    buf.clear();
                }
                
                canal.position(posicao);
                System.out.println("Escreve na posi��o: " + canal.position() + " | Passos: "+ passos + " | "+
                        (passos + custoBusca(colisao)));
                // Op��o I: apenas escrever na posicao encontrada
                //if ( (passos + custoBusca(colisao)) < (1 + TODO ) )
                //canal.write(a.getByteBuffer());
                buf.clear();
            }

        } catch (IOException ex) {
            Logger.getLogger(ManipuladorSequencial.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        System.out.println("Aluno adicionado!");
    }
    
    @Override
    public Aluno delReg(int matric) {
        ByteBuffer buf = ByteBuffer.allocate(157);
        // Informa��es do aluno removido
        Aluno removido = null;
        
        try {
            canal.position(0);
            
            // Posi��o do registro a ser removido (-1000 � n�o encontrado)
            long posicaoRegistro = -1000;
            // Posi��o do �ltimo registro do arquivo
            long posicaoUltimo = canal.size() - 157;
            // N�mero total de registros existentes no arquivo
            int total = (int) (canal.size()/157); 
            // Itera��o na qual o registro a ser removido foi encontrado
            int aux = 0;
            
            for (int i = 0; i < total; i++) {
                canal.read(buf);
                buf.flip();
                int x = buf.getInt();
                // Encontrada a matr�cula referente ao registro a ser removido
                if (x == matric) {
                    posicaoRegistro = canal.position() - 157;
                    aux = i;
                    buf.clear();
                    removido = new Aluno(buf);
                    break;
                }
                buf.clear();
            }
            
            // Se o registro foi encontrado
            if (posicaoRegistro != -1000) {
                if(posicaoRegistro == posicaoUltimo){
                    // � o �ltimo registro, apenas diminua o tamanho do arquivo
                    canal.truncate(canal.size() - 157);
                }
                else { 
                    // Limpa o buffer pra reutilizar
                    buf.clear();
                    // Puxa os registros a partir do removido para uma posi��o acima
                    for(int i = aux; i <= total; i++){
                        canal.position((i+1)*157);
                        canal.read(buf);
                        buf.flip();
                        canal.position(i*157);
                        canal.write(buf);
                        buf.clear();
                    }
                    canal.truncate(canal.size() - 157);
                }
            }
            else System.out.println("Aluno inexistente!");
            
        } catch (IOException ex) {
            Logger.getLogger(ManipuladorSequencial.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return removido;
    }
    
    @Override
    public Aluno getReg(int matric) {
        ByteBuffer buf = ByteBuffer.allocate(TAMANHO_REGISTRO);
        
        int hash = calculaHash(matric);
        int posicao = hash * TAMANHO_REGISTRO;

        try {
            canal.position(posicao);
            canal.read(buf);
            buf.flip();
            int x = buf.getInt();
            buf.clear();
            if (x == matric) {
                Aluno a = new Aluno(buf);
                return a;
            }
            else {
                int incremento = calculaIncremento(matric);
                while(x != 0) {
                    posicao = (((posicao/TAMANHO_REGISTRO) + incremento) % this.P) * TAMANHO_REGISTRO;
                    canal.position(posicao);
                    canal.read(buf);
                    buf.flip();
                    x = buf.getInt();
                    buf.clear();
                    if (x == matric) {
                        Aluno a = new Aluno(buf);
                        return a;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int custoBusca(int matricula) {
        ByteBuffer buf = ByteBuffer.allocate(TAMANHO_REGISTRO);
        
        int hash = calculaHash(matricula);
        int posicao = hash * TAMANHO_REGISTRO;
        int passos = 1;
        
        try {
            canal.position(posicao);
            canal.read(buf);
            buf.flip();
            int x = buf.getInt();
            buf.clear();
            
            if (x == matricula) return passos;
            else {
                int incremento = calculaIncremento(matricula);
                while(x != 0) {
                    passos++;
                    posicao = (((posicao/TAMANHO_REGISTRO) + incremento) % this.P) * TAMANHO_REGISTRO;
                    canal.position(posicao);
                    canal.read(buf);
                    buf.flip();
                    x = buf.getInt();
                    buf.clear();
                    if (x == matricula) break; 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return passos;
    }
    
    public int[] lerSelecionados() {
        ByteBuffer buf = ByteBuffer.allocate(4);
        int[] selected = new int[1000];
        
        try {
            canal.position(0);
            for (int i = 0; i < canal.size()/4; i++) {
                canal.read(buf);
                buf.flip();
                int x = buf.getInt();
                selected[i] = x;
                buf.clear();
            }
            return selected;
            
        } catch (IOException ex) {
            Logger.getLogger(ManipuladorSequencial.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void inicializaArquivo(Aluno vazio) {        
        try {
            canal.position(0);
            for (int i = 0; i < this.P; i++)
                canal.write(vazio.getByteBuffer());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void listarArquivo() {
        ByteBuffer buf = ByteBuffer.allocate(TAMANHO_REGISTRO);
        try {
            canal.position(0);
            for (int i = 0; i < this.P; i++) {
                canal.read(buf);
                buf.flip();
                int x = buf.getInt();
                System.out.println("[ " + i + " ] " + x);
                buf.clear();
            }
        } catch (IOException ex) {
            Logger.getLogger(ManipuladorSequencial.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
}
