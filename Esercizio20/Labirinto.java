package Esercizio20;

import java.util.*;

public class Labirinto
{

    public static void main(String... args)
    {
        System.out.println("Regole del gioco:\n1) Utilizza i tasti WASD per muoverti.\n2) Raggiungi l'uscita E");
        System.out.println("\n\nGioca !!!");

        int dim=generaInteroRandom(5);
        char[][] matrice=new char[dim][dim]; // initialize campo da gioco


        int[] exit; // initialize EXIT
        int[] player; // initialize PLAYER
        int [][][] padri=new int [dim][dim][2]; // matrice dei padri che tiene traccia del predecessore di ogni posizione
        Stack<int[]> path = new Stack<>(); // lista contenente il cammino;

        do {
            generaMatrice(matrice,dim); // riempie la matrice generando la base di gioco
            exit = ridefinisciCasella(matrice, 'E', dim); // posizione exit salvata come [riga,colonna]
            player = ridefinisciCasella(matrice, 'P', dim); // posizione giocatore salvata come [riga,colonna]
            stampaMatrice(matrice,dim);
        }while(!findPath(matrice,padri,path,dim,player,exit));

        System.out.println();
        stampaMatrice(matrice,dim);

        Scanner scan=new Scanner(System.in);
        String input;
        String answer;

        System.out.println();
        System.out.print("Let the computer resolve the labyrinth (Y/N)? -> ");
        do{
            answer=scan.next().toLowerCase();
        }while(!answer.equals("y") && !answer.equals("n"));

        /*for(int row=0;row<dim;row++)
        {
            for(int collumn=0;collumn<dim;collumn++)
            {
                System.out.print("|"+padri[row][collumn][0]+" - "+padri[row][collumn][1]);
            }
            System.out.print("|");
            System.out.println();
        }
        System.out.println();
        System.out.println();
        */ //Stampa della matrice dei padri

        /*
        for(int[] e: path)System.out.print("|"+e[0]+" - "+e[1]);
        System.out.println();
        */ //Stampa del cammino che si trova in path

        do
        {
            if(answer.equals("n"))input=inputMovimento(scan);
            else
            {
                input=autoMove(path,player);
                pausaAutoPlay();
            }
            if(!checkOutOfBounds(input,player,dim))
            {
                if(!checkCollision(matrice,player,input))
                {
                    aggiornaMatrice(matrice,input,player);
                    clearConsole();
                    stampaMatrice(matrice,dim);
                }
                else System.out.println("Non dare testate al muro!");
            }
            else System.out.println("Hey! Non scappare dal campo da gioco!");
            if(checkIfWin(player,exit))
            {
                winnerMessage();
                break;
            }

        }while(true);

    }

    public static void pausaAutoPlay()
    {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String autoMove(Stack<int []>path, int[] player)
    {
        if(!path.isEmpty())
        {
            int[] element=path.pop();
            if(player[0]==element[0]) {
                if (player[1] - element[1] == 1) return "a";
                else return  "d";
            }
            else if(player[0] - element[0] == 1) return  "w";
            else return  "s";
        }
        return "";
    }

    public static int generaInteroRandom(int bound)
    {
        Random rand=new Random();
        return bound+rand.nextInt(bound);
    }

    public static void winnerMessage()
    {
        System.out.println("\n");
        System.out.println(" _________________________________");
        System.out.println("|--------> HAI VINTO !!! <--------|");
        System.out.println(" ‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾‾");
    }

    public static void clearConsole()
    {
        System.out.flush();
    }

    public static boolean checkCollision(char[][] matrix,int[] P,String input)
    {
        switch (input){
            default:
            case "w":
                if(matrix[P[0]-1][P[1]]=='W')
                {
                    return true;
                }
                break;
            case "a":
                if(matrix[P[0]][P[1]-1]=='W')
                {
                    return true;
                }
                break;
            case "s":
                if(matrix[P[0]+1][P[1]]=='W')
                {
                    return true;
                }
                break;
            case "d":
                if(matrix[P[0]][P[1]+1]=='W')
                {
                    return true;
                }
                break;
        }
        return false;
    }

    public static void aggiornaMatrice(char[][] matrix,String input,int[] P)
    {
        matrix[P[0]][P[1]]='-';
        switch (input) {
            case "w" -> P[0]--;
            case "a" -> P[1]--;
            case "s" -> P[0]++;
            case "d" -> P[1]++;
        }
        matrix[P[0]][P[1]] = 'P';
    }

    public static boolean checkIfWin(int [] P,int [] E)
    {
        return (P[0] == E[0] && P[1] == E[1]);
    }

    public static boolean checkOutOfBounds(String input, int[] P,int dim)
    {
        switch (input){
            default:
            case "w":
                if(P[0]!=0)
                {
                    return false;
                }
                break;
            case "a":
                if(P[1]!=0)
                {
                    return false;
                }
                break;
            case "s":
                if(P[0]!=dim-1)
                {
                    return false;
                }
                break;
            case "d":
                if(P[1]!=dim-1)
                {
                    return false;
                }
                break;
        }
        return true;
    }

    public static String inputMovimento(Scanner scan)
    {
        String input;
        System.out.println("\n");
        System.out.print("Direzione: ");
        do
        {
            input =scan.next().toLowerCase();

        }while(!validMove(input));
        return input;
    }

    public static boolean validMove(String input)
    {
        return (input.equals("w") || input.equals("a") || input.equals("s") || input.equals("d"));
    }

    public static int[] ridefinisciCasella(char[][] matrix, char charInput,int dim)
    {
        Random rand=new Random();
        boolean blocked;
        int eRow,eCol;
        do
        {
            eRow = rand.nextInt(dim-1);
            eCol = rand.nextInt(dim-1);
            blocked=checkIfUnreachable(matrix,eRow,eCol,dim);

        }while(blocked);
        matrix[eRow][eCol]=charInput;
        return new int[]{eRow,eCol};
    }

    public static boolean findPath(char[][] matrix,int [][][] padri,Stack<int[]>cammino, int dim, int[]P, int[]E)
    {
        if(Arrays.equals(P, E))return false; // esiste la posibilità che le posizioni siano le stesse
        Queue<int[]> coda = new LinkedList<>(); // coda contenente gli elementi da visitare
        List<int[]> vis = new ArrayList<>(); // array di posizioni già visitate della matrice
        coda.add(P);
        padri[P[0]][P[1]]= P; //root
        return bfs(matrix,dim,E,P,coda,cammino,vis,padri);
    }

    public static boolean bfs(char[][] matrix, int dim, int[]E,int[]P, Queue<int[]> coda,Stack<int[]> cammino, List<int[]> vis,int [][][] padri)
    {
        while(!coda.isEmpty())
        {
            int[] posizione=coda.remove();
            if(Arrays.equals(posizione, E))
            {

                for(int row=0;row<dim;row++)
                {
                    for(int collumn=0;collumn<dim;collumn++)
                    {
                        System.out.print("|"+padri[row][collumn][0]+" - "+padri[row][collumn][1]);
                    }
                    System.out.print("|");
                    System.out.println();
                }
                System.out.println();
                System.out.println();

                while(!Arrays.equals(posizione, P)){
                    cammino.push(posizione);
                    System.out.println("Giocatore: "+P[0]+" - "+P[1]);
                    System.out.println("Padre: "+padri[posizione[0]][posizione[1]][0]+" - "+padri[posizione[0]][posizione[1]][1]);
                    posizione=new int[]{padri[posizione[0]][posizione[1]][0],padri[posizione[0]][posizione[1]][1]};
                    //System.out.println("Post: "+posizione[0]+" - "+posizione[1]);
                }
                return true; // esiste un cammino
            }

            boolean visited=false;

            for(int[] coordinate:vis)
            {
                if(Arrays.equals(coordinate, posizione))
                {
                    visited=true;
                    break;
                }
            }
            if(!visited)
            {
                addAdjacents(matrix,coda,padri,posizione,dim);
                vis.add(posizione);
            }

        }
        return false; // non esiste un cammino
    }

    public static void addAdjacents(char[][] matrix,Queue<int[]> coda,int [][][] padri, int[]posizione,int dim)
    {
        if((posizione[0]>0 && posizione[0]<dim-1) && (posizione[1]>0 && posizione[1]<dim-1)) // sta all'interno della matrice
        {
            addW(matrix,coda,padri,posizione);
            addA(matrix,coda,padri,posizione);
            addS(matrix,coda,padri,posizione);
            addD(matrix,coda,padri,posizione);
        }
        else if((posizione[0]==0) && (posizione[1]==0)) // angolo sinistro in alto
        {
            addS(matrix,coda,padri,posizione);
            addD(matrix,coda,padri,posizione);
        }
        else if((posizione[0]==0) && (posizione[1]>0 && posizione[1]<dim-1)) // riga sopra ma non sugli angoli
        {
            addA(matrix,coda,padri,posizione);
            addS(matrix,coda,padri,posizione);
            addD(matrix,coda,padri,posizione);
        }
        else if((posizione[0]==0) && (posizione[1]==dim-1)) // angolo destro alto
        {
            addA(matrix,coda,padri,posizione);
            addS(matrix,coda,padri,posizione);
        }
        else if((posizione[0]>0 && posizione[0]<dim-1) && (posizione[1]==dim-1)) // colonna destra ma non sugli angoli
        {
            addW(matrix,coda,padri,posizione);
            addA(matrix,coda,padri,posizione);
            addS(matrix,coda,padri,posizione);
        }
        else if((posizione[0]==dim-1) && (posizione[1]==dim-1)) // angolo destro in basso
        {
            addW(matrix,coda,padri,posizione);
            addA(matrix,coda,padri,posizione);
        }
        else if((posizione[0]==dim-1) && (posizione[1]>0 && posizione[1]<dim-1)) // riga sotto ma non sugli angoli
        {
            addW(matrix,coda,padri,posizione);
            addA(matrix,coda,padri,posizione);
            addD(matrix,coda,padri,posizione);
        }
        else if((posizione[0]==dim-1) && (posizione[1]==0)) // angolo sinistro in basso
        {
            addW(matrix,coda,padri,posizione);
            addD(matrix,coda,padri,posizione);
        }
        else if((posizione[0]>0 && posizione[0]<dim-1) && (posizione[1]==0)) // colonna sinistra ma non sugli angoli
        {
            addW(matrix,coda,padri,posizione);
            addS(matrix,coda,padri,posizione);
            addD(matrix,coda,padri,posizione);
        }
    }

    public static void addW(char[][] matrix,Queue<int[]> coda, int [][][] padri, int[]P)
    {
        if(matrix[P[0]-1][P[1]]!='W')
        {
            coda.add(new int[]{P[0]-1, P[1]});
            if(Arrays.equals(padri[P[0] - 1][P[1]], new int[]{0, 0}))
            {
                padri[P[0]-1][P[1]]=new int[]{P[0],P[1]};
            }
        }

    }

    public static void addA(char[][] matrix,Queue<int[]> coda, int [][][] padri, int[]P)
    {
        if(matrix[P[0]][P[1]-1]!='W')
        {
            coda.add(new int[]{P[0], P[1] - 1});
            if(Arrays.equals(padri[P[0]][P[1]-1], new int[]{0, 0}))
            {
                padri[P[0]][P[1] - 1] =new int[]{P[0],P[1]};
            }
        }
    }

    public static void addS(char[][] matrix,Queue<int[]> coda, int [][][] padri, int[]P)
    {
        if(matrix[P[0]+1][P[1]]!='W')
        {
            coda.add(new int[]{P[0] + 1, P[1]});
            if(Arrays.equals(padri[P[0]+1][P[1]], new int[]{0, 0}))
            {
                padri[P[0] + 1][P[1]] =new int[]{P[0],P[1]};
            }
        }
    }

    public static void addD(char[][] matrix,Queue<int[]> coda, int [][][] padri, int[]P)
    {
        if(matrix[P[0]][P[1]+1]!='W')
        {
            coda.add(new int[]{P[0], P[1] + 1});
            if(Arrays.equals(padri[P[0]][P[1]+1], new int[]{0, 0}))
            {
                padri[P[0]][P[1]+1]=new int[]{P[0],P[1]};
            }
        }
    }

    public static boolean checkIfUnreachable(char[][] matrix,int row,int col,int dim)
    {
        if((row>0 && row<dim-1) && (col>0 && col<dim-1)) // sta all'interno della matrice
        {
            return matrix[row - 1][col] == 'W' && matrix[row][col - 1] == 'W' && matrix[row + 1][col] == 'W' && matrix[row][col + 1] == 'W';
        }
        else if((row==0) && (col==0)) // angolo sinistro in alto
        {
            return matrix[row + 1][col] == 'W' && matrix[row][col + 1] == 'W';
        }
        else if((row==0) && (col>0 && col<dim-1)) // riga sopra ma non sugli angoli
        {
            return matrix[row][col - 1] == 'W' && matrix[row + 1][col] == 'W' && matrix[row][col + 1] == 'W';
        }
        else if((row==0) && (col==dim-1)) // angolo destro alto
        {
            return matrix[row + 1][col] == 'W' && matrix[row][col + 1] == 'W';
        }
        else if((row>0 && row<dim-1) && (col==dim-1)) // colonna destra ma non sugli angoli
        {
            return matrix[row - 1][col] == 'W' && matrix[row][col - 1] == 'W' && matrix[row + 1][col] == 'W';
        }
        else if((row==dim-1) && (col==dim-1)) // angolo destro in basso
        {
            return matrix[row - 1][col] == 'W' && matrix[row][col - 1] == 'W';
        }
        else if((row==dim-1) && (col>0 && col<dim-1)) // riga sotto ma non sugli angoli
        {
            return matrix[row - 1][col] == 'W' && matrix[row][col - 1] == 'W' && matrix[row][col + 1] == 'W';
        }
        else if((row==dim-1) && (col==0)) // angolo sinistro in basso
        {
            return matrix[row - 1][col] == 'W' && matrix[row][col + 1] == 'W';
        }
        else if((row>0 && row<dim-1) && (col==0)) // colonna sinistra ma non sugli angoli
        {
            return matrix[row - 1][col] == 'W' && matrix[row + 1][col] == 'W' && matrix[row][col + 1] == 'W';
        }
        return false;
    }

    public static void stampaMatrice(char[][] matrix,int dim)
    {
        System.out.println();
        for(int row=0;row<dim;row++)
        {
            for(int collumn=0;collumn<dim;collumn++)
            {
                System.out.print("|"+matrix[row][collumn]);
            }
            System.out.print("|");
            System.out.println();
        }
    }

    public static void generaMatrice(char[][] matrix,int dim)
    {
        char[] alphabet={'W','W','W','-','-','-','-','-'};//
        Random rand=new Random();
        for(int row=0;row<dim;row++)
        {
            for(int collumn=0;collumn<dim;collumn++)
            {
                matrix[row][collumn]=alphabet[rand.nextInt(8)];
            }
        }
    }
}
