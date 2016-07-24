/* *****************************************************************************
 * C�digos desenvolvidos pelos seguintes alunos
 *
 * @author Claudson Bispo Martins Santos    201410042132
 * @author Edgar Vieira Lima Neto           201410042150
 * @author Guilherme Boroni Pereira         201410042197
 * ****************************************************************************/

package ed2;
/**
 * Interface gen�rica que define as opera��es de organizadores de arquivos
 * de alunos em disco.
 * 
 * @author Tarcisio Rocha
 *
 */
public interface FileOrganizer {
    
    /**
     * Dada uma inst�ncia da classe Aluno, este m�todo adiciona os dados
     * da inst�ncia em um arquivo seguindo o m�todo de organiza��o
     * de arquivos especificado.
     * 
     * @param a Inst�ncia da classe Aluno
     */
    public void addReg(Aluno a);
    
    /**
     * Dado um n�mero de matr�cula, localiza e exclui o registro do arquivo
     * 
     * @param matric Matr�cula do aluno a ser exclu�do
     * @return Inst�ncia da classe Aluno correspondente ao aluno exclu�do
     */
    public Aluno delReg(int matric);
    
    /**
     * Dado um n�mero de matr�cula, este m�todo consulta o arquivo de alunos e
     * devolve uma inst�ncia que encapsula os dados do aluno com a referida
     * matr�cula.
     * 
     * @param matric N�mero de matr�cula para a consulta
     * @return Inst�ncia da classe Aluno, Null se for inexistente
     */
    public Aluno getReg(int matric);
}
