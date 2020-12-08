package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import connexion.Appels;
import hw.GestionMonScoreRemote;

public class PanelJeu extends JPanel implements Runnable, MouseListener{
	//Mon EJB
	//GestionMonScoreRemote ejb;
	
	//private JPanel contentPane;
	Jewels jeu;
    Thread runner;
	MediaTracker tracker;	//stockage des images en chargement

    Image jewelImg[] = new Image[10];

    int curX, curY;	//joyau selectionnée 1
    int selX, selY;	//joyau selectionnée 2
    int nbX, nbY;	//parametre nombre de colonne /rangée
    int nbJoyaux;	//parametre nombre de type de joyaux

	int type[][] = new int[20][20];	//type de joyaux
	int stat[][] = new int[20][20];	//etat du joyaux : négatif = marqué
	int dirX[][] = new int[20][20];	//sa direction horizontale
	int dirY[][] = new int[20][20];	//sa direction verticale

	int next[] = new int[20];		//la nouvelle rangée

	String pseudo;
	int score;		//score du joueur
	int mult;		//multiplicateur de point
	int bonus;		//points bonus tous les 20 multiplicateurs

	int timing;		//temps restant
	int anims;		//votesse de temporisation des animations
	int vitesse;	//vitesse du chrono
	int tempo;		//temporisateur

 	int compteur;	//variable qui permet de faire clignoter
 					//permet de décaler les pierres lors de la chute
	int sens;		//0 = inverse les 2 joyaux, 1=fait reprendre leur etat original, -1:aucun
 	int gameState;	//prend la valeure ci dessous

 	int decX, decY;	//décalage pour centrer les textes sur l'écran
 	int scrX, scrY;	//taille de l'écran
 	

	final static int MODE_NORMAL = 0;
	final static int MODE_EXCHANGE = 1;
	final static int MODE_DELETED = 2;
	final static int MODE_BLINKING = 3;
	final static int MODE_FALLING = 4;
	final static int MODE_LOOSE = 9;
	/**
	 * Create the panel.
	 */
	public PanelJeu(String pseudo) {
		System.out.println("cons pseudo:"+pseudo);
		this.pseudo= pseudo;
		System.out.println("cons this.pseudo"+this.pseudo);
		
		//this.ejb = ejb;
		// Vitesse du chrono
				//this.jeu=jeu;
				vitesse = 2500;
				// Vitesse animation
				anims=16;
				// Nb colonnes
				nbX=4;
				// Nb lignes
				nbY=4;
				// Nb joyaux
				nbJoyaux=7;
				//Dimension screenSize = contentPane.getSize();
				scrX = 450;
				scrY = 300;

				tracker = new MediaTracker(this);
				for(int i = 0; i<9; i++){
					File file = new File("Images\\JEWEL"+i+".GIF");
			        try
			        {
			        	jewelImg[i] = ImageIO.read(file);
			        } 
			        catch (IOException e) 
			        {
			            e.printStackTrace();
			        }
					tracker.addImage(jewelImg[i], 0);		// Lier MediaTracker avec image
					try
					{
						tracker.waitForAll();				//Attendre le chargement complet de l'image
					}catch (InterruptedException e) {}
				}

				//rentrer des valeurs aleatoires
				for(int i = 0; i < nbX; i++){
					for(int j = 0; j < nbY; j++){
						type[i][j] = (int)(Math.random() * nbJoyaux)+1;
					}
				}
				//initialise les valeurs
				curX = -1;
				curY = -1;
				selX = -1;
				selY = -1;

				score = 0;
				timing = 10;
				compteur = 0;
				sens = -1;
				
				addMouseListener(this);
				repaint();
				gameState = MODE_DELETED;	//enlever les grappes déja présentes...
				//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setBounds(100, 100, 450, 300);
				setBorder(new EmptyBorder(5, 5, 5, 5));
				setLayout(new BorderLayout(0, 0));
				//contentPane = new JPanel();
				//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
				//contentPane.setLayout(new BorderLayout(0, 0));
				//setContentPane(contentPane);
	}
	// ----------------------------------------------------------------------------
	public void Rotate(int X1, int Y1, int X2, int Y2){
		//intervertir deux pieces ensemble
		int a = type[X1][Y1];
		type[X1][Y1] = type[X2][Y2];
		type[X2][Y2] = a;
	}

	// ----------------------------------------------------------------------------
	public int Marquer_Group()
	{//marquer le plateau de toutes les grappes de pierre
		int n = 0;

		for (int j=0; j < nbY; j++){
			for (int i=0; i < nbX; i++){
				dirX[i][j] = 0;
				dirY[i][j] = 0;
				stat[i][j] = Tester_Jewel(i, j ,type[i][j]);
				if( i + 1 <= nbX-1 && (type[i+1][j] == type[i][j]) ){
					if(Tester_Valid( Tester_Jewel(i+1, j ,type[i][j]),1)){stat[i][j] = -Math.abs(stat[i][j]);}
				}
				if( j - 1 >= 0 && (type[i][j-1] == type[i][j]) ){
					if ( Tester_Valid(Tester_Jewel(i, j-1 ,type[i][j]),2)){ stat[i][j] = -Math.abs(stat[i][j]);}
				}
				if( i - 1 >= 0 && (type[i-1][j] == type[i][j]) ){
					if ( Tester_Valid(Tester_Jewel(i-1, j ,type[i][j]),3)){ stat[i][j] =-Math.abs(stat[i][j]);}
				}
				if( j + 1 <= nbY-1 && (type[i][j+1] == type[i][j]) ){
					if ( Tester_Valid(Tester_Jewel(i, j+1 ,type[i][j]),4)){ stat[i][j] = -Math.abs(stat[i][j]);}
				}
				if ( Tester_Valid( stat[i][j] ,1) && Tester_Valid( stat[i][j] ,3) ){
					stat[i][j] = -Math.abs( stat[i][j] );
					n++;
				}
				if ( Tester_Valid( stat[i][j] ,2) && Tester_Valid( stat[i][j] ,4) ){
					stat[i][j] = -Math.abs( stat[i][j] );
					n++;
				}
			}
		}
//		System.out.println("il reste "+n+" joyaux alignés");
		return n;
	}
	// ----------------------------------------------------------------------------
	public int Tester_Jewel(int MyX , int MyY , int MyCol){
		//tester toute les pieces une à une et marque si un joyau adjacent est du meme type
		int a=0;
		if( MyX + 1 <= nbX-1 ){
		if( type[MyX + 1][MyY] != MyCol ){
			a+=1;
		}else{ a+=9; }
		}else{ a+=1; }
		if ( MyY - 1 >= 0 ){
		if( type[MyX][MyY-1] != MyCol ){
			a+=10;
		}else{ a+=90; }
		}else{ a+=10; }
		if ( MyX - 1 >= 0 ){
		if( type[MyX-1][MyY] != MyCol ){
			a+=100;
		}else{ a+=900; }
		}else{ a+=100; }
		if ( MyY + 1 <= nbY-1 ){
		if( type[MyX][MyY+1] != MyCol ){
			a+=1000;
		}else{ a+=9000; }
		}else{ a+=1000; }
		return a;
	}
	// ----------------------------------------------------------------------------
	public boolean Tester_Valid(int x, int n){
		//permet de tester dans x si la prochaine le chiffre est un 9 à la postion n
		x = Math.abs(x);
		switch(n){
			case 2:
				n=10;
				break;
			case 3:
				n=100;
				break;
			case 4:
				n=1000;
				break;
			default:
				n=1;
		}
		if ( (x % (n*10))/n == 9 ){
		  	return true;
		}else{
		  	return false;
		}
	}
	// ----------------------------------------------------------------------------
	public void raz_Selection(){
		curX = -1;
		curY = -1;
		selX = -1;
		selY = -1;
	}

	// ----------------------------------------------------------------------------
	public int delete_Jewel(){
		//supprime les pierres marquées et renvoie le nombre
		int n = 0;

		for(int i = 0; i < nbX; i++){
			for(int j = 0; j < nbY; j++){
				if(stat[i][j] < 0){
					n++;
					type[i][j] = 0;
					stat[i][j] = 0;
					dirX[i][j] = 0;
					dirY[i][j] = 0;

				}else{
					stat[i][j] = 1;
				}
			}
		}
		return n;	//renvoie le nombre de joyau marqués
	}

	// ----------------------------------------------------------------------------
	public int compter_Delete(){
		//compter combien il reste de joyaux marqués
		int n = 0;
		for(int i = 0; i < nbX; i++){
			for(int j = 0; j < nbY; j++){
				if(stat[i][j] <= 0){
					n++;
				}
			}
		}
		return n;
	}

	// ----------------------------------------------------------------------------
	public void tomber_Jewel(){
		//décaler d'un cran vers le bas les joyaux

		for(int i = 0; i < nbX; i++)
		{
			for(int j = nbY-1; j >= 0; j--)
			{
				if( j == 0 ){
					if(stat[i][j] == 0 || dirY[i][j] != 0){
						//mettre un nouveau joyau
						next[i] = (int)(Math.random() * nbJoyaux) + 1;
					}
				}else{
					if( stat[i][j] == 0 && stat[i][j-1] == 1)
					{
						//decaler celui du haut vers le bas
						dirY[i][j-1] = (int)Math.floor(32 / anims);
					}
					if(dirY[i][j] != 0){
						//decendre aussi celui la
						dirY[i][j-1] = (int)Math.floor(32 / anims);
					}
				}
			}
		}
	}

	// ----------------------------------------------------------------------------
	public void decaler_Jewel(){
		//décale les billes vers le bas
		//compte les zones marqué de la rangée

		for(int i = 0; i < nbX; i++)
		{
			for(int j = nbY-1; j >= 0; j--)
			{
				//System.out.println("i col :"+i+" j row :"+j);
				if( j == 0 ){
					if (next[i] != 0 )
					{
							//faire tomber ce joyaux vers le bas
							type[i][j] = next[i];
							stat[i][j] = 1;
					}
				}else{
					if (dirY[i][j-1] != 0 )
					{
						type[i][j] = type[i][j-1];
						stat[i][j] = stat[i][j-1];
					}
				}
				dirY[i][j] = 0;
				dirX[i][j] = 0;
			}
			next[i] = 0;
		}
	}

	// ----------------------------------------------------------------------------
  public void paint (Graphics screen) {
	int a, b;
	Image offscreenImg = createImage(scrX, scrY);
    Graphics  offscreen = offscreenImg.getGraphics();


	//peindre l'écran
    offscreen.setColor(new Color(0,120,0));
	offscreen.fillRect(0 , 0, scrX, scrY);

	//peindre le damier
	for(int i = 0; i < nbX; i++){
  		for(int j = 0; j < nbX; j++){
			if ( ( (j%2) + i ) %2 == 0 ){
				//les cases paires en noir
				offscreen.setColor( Color.black );
			}else{
				//les cases impair en gris
				offscreen.setColor( new Color(92,92,92) );
			}
			offscreen.fillRect(i*32+32, j*32+32 , 32, 32);
		}
	}

	offscreen.setColor(Color.yellow);
	//peindre les pierres
	for(int i = 0; i < nbX; i++)
	{
		//offscreen.drawString(String.valueOf(i), i*32+45, 25); //debog
  		for(int j = 0; j < nbY; j++)
  		{
			//offscreen.drawString(String.valueOf(j), 15, j*32+55); //debog
			int x = type[i][j];
			//peindre les joyaux
			switch (gameState){
				case MODE_NORMAL:
					//prendre la case mode_normal
				case MODE_DELETED:
					//état normal de démarage
					a = i*32+32;
					b = j*32+32;
					offscreen.drawImage(jewelImg[x], a, b, this);
					break;
				case MODE_EXCHANGE:
					//inverser les joyaux
					a = 32 + i * 32 + dirX[i][j] * compteur;
					b = 32 + j * 32 + dirY[i][j] * compteur;
					offscreen.drawImage(jewelImg[x], a , b, this);
					break;
				case MODE_BLINKING:
					//faire clignoter les groupes de pierre
					a = 32 + i * 32 + dirX[i][j] * compteur;
					b = 32 + j * 32 + dirY[i][j] * compteur;

					if (stat[i][j] < 0){
						//peindre le fond en blanc si compteur est pair
						if (compteur % 2 == 0){
							offscreen.setColor(Color.white);
							offscreen.fillRect(i*32+32, j*32+32 , 32, 32);
						}
					}
					offscreen.drawImage(jewelImg[x], i*32+32, j*32+32, this);
					//affiche les points gagnés
					if(stat[i][j] < 0 && compteur < anims){
						int n = mult * 10;
						offscreen.setFont(new Font("Arial", Font.BOLD, 20));
						//faire un petit effet de clignotement au score
						if(compteur % 2 == 0){
							offscreen.setColor(Color.red);
						}else{
							offscreen.setColor(Color.yellow);
						}
						offscreen.drawString(String.valueOf(n), 32 + i * 32 + dirX[i][j], 32 + j * 32 + dirY[i][j]);
					}
					break;
				case MODE_FALLING:
					//faire tomber les pierres
					a = 32 + i * 32;
					b = 32 + j * 32 + dirY[i][j] * compteur;
					if(x !=0 ){
						offscreen.drawImage(jewelImg[x], a , b, this);
					}
					int k = next[i];
					if(k !=0 ){
						offscreen.drawImage(jewelImg[k], a , (int)Math.floor(32 / anims) * compteur, this);
					}
					break;
				case MODE_LOOSE:
					//faire tomber les joyaux hors de l'écran
					a = 32 + i * 32 + dirX[i][j];
					b = 32 + j * 32 + dirY[i][j] * compteur;
					offscreen.drawImage(jewelImg[x], a , b, this);
					//afficher le texte qui défile de droite a gauche puis de gauche a droite
					offscreen.setColor(Color.black);
					offscreen.setFont(new Font("Arial", Font.BOLD, 30));
					offscreen.drawString("C'est fini", 51+timing, 201);
					offscreen.drawString("votre score", 41+timing, 231);
					offscreen.drawString(score+" pts",  71+timing, 261);
					offscreen.setColor(Color.white);
					offscreen.setFont(new Font("Arial", Font.BOLD, 30));
					offscreen.drawString("C'est fini", 50+timing, 200);
					offscreen.drawString("votre score", 40+timing, 230);
					offscreen.drawString(score+" pts",  70+timing, 260);
					break;

			}
			if(i == curX && j ==curY){
				//mettre un cadre au dessus du joyau selectionné
				offscreen.setColor(Color.yellow);
				offscreen.drawRect(i*32+32, j*32+32, 32, 32);
			}
			if(i == selX && j ==selY){
				//mettre un cadre au dessus du joyau selectionné
				offscreen.setColor(Color.white);
				offscreen.drawRect(i*32+32, j*32+32, 32, 32);
			}
			//débogage état de la pierre
			/*
			offscreen.setColor(Color.yellow);
			offscreen.drawString(String.valueOf(stat[i][j]), i*32+30, j*32+44);
			offscreen.drawString(String.valueOf(dirX[i][j]), i*32+30, j*32+54);
			offscreen.drawString(String.valueOf(dirY[i][j]), i*32+40, j*32+54);
			*/
		}
	}
	
	offscreen.setFont(new Font("Arial", Font.BOLD, 12));
	offscreen.drawString("game gameState : "+String.valueOf(gameState), 10, 400);
	offscreen.drawString("compteur : "+String.valueOf(compteur), 10, 420);
	offscreen.drawString("sel : "+selX+","+selY, 300, 400);
	offscreen.drawString("cur : "+curX+","+curY, 300, 415);
	offscreen.drawString("sens : "+sens, 300, 430);

	//peindre les barres de progression
	int x = (int)Math.floor( (scrX-(nbX*32)) / 2);
	if(gameState != MODE_LOOSE){
		offscreen.setColor(Color.yellow);
		offscreen.fillRect(x+100 , 5, 100, 18);
		offscreen.fillRect(x+210 , 5, 100, 18);
		offscreen.setColor(Color.red);
		offscreen.fillRect(x+100 , 5, 5 * bonus, 18);
		offscreen.fillRect(x+210 , 5, 100 - (int)Math.floor(timing / 3), 18);
		offscreen.setColor(Color.black);
		offscreen.drawRect(x+100 , 5, 100, 18);
		offscreen.drawRect(x+210 , 5, 100, 18);
		//offscreen.setColor(Color.cyan);
		offscreen.setFont(new Font("Arial", Font.BOLD, 16));
		offscreen.drawString("bonus : "+String.valueOf(20-bonus), x+110, 20);
		offscreen.drawString("time : " +String.valueOf(timing), x+220, 20);
	}
	//texte des barres
	offscreen.setFont(new Font("Arial", Font.BOLD, 16));
	offscreen.setColor(Color.yellow);
	offscreen.drawString("Score : "+String.valueOf(score), x-10, 20);
	offscreen.setFont(new Font("Arial", Font.BOLD, 25));
	offscreen.drawString("JEWELS", (int)Math.floor((scrX-20)/2), scrY-15);
    screen.drawImage(offscreenImg, 0, 0, this);
	offscreen.dispose();	//détruire l'image hors écran inutile

  }
	// ----------------------------------------------------------------------------
  public void run() {
		//boucle de jeu
        Thread thisThread = Thread.currentThread();
        while (runner == thisThread) {
			switch (gameState){
				case 0:
					//etat normal : décrémenter le chrono
					mult = 0;
					pause(vitesse);
					timing--;
					if(timing < 0){
						//zavez perdu !
						//ejb.UpdateOnDatabase();
						try {
							System.out.println("psych: "+score);
							jeu.EnregistrerPartie(score);
							jeu.FinPartie(score);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						gameState = MODE_LOOSE;
						sens = 1; //faits defiler le texte a droite
					}
					compteur = 0;
					break;
				case MODE_EXCHANGE:	//inverser les joyaux selectionné
					compteur++;
					if ( compteur >= anims)
					{
						//inverser les joyau précedement inversé
						//vérifie qu'on enleve une grappe
						if( sens == 0){
							//le joueur a choisit d'inverser 2 joyaux
//							System.out.println("inverser les 2 joyaux :"+ sens);
							Rotate(selX, selY, curX, curY);
						}else{
							//les joyaux doivent reprendre leur état initiale
//							System.out.println("fait reprendre l'état original :"+ sens);
							Rotate(curX, curY, selX, selY);
						}
						compteur = 0;
						gameState = MODE_DELETED;		//passer en clignotement des groupes
					}
					pause(anims);
					break;
				case MODE_DELETED:	//inverser les joyaux selectionné
					int a = 0;
					int b = 0;
					int c = 0;
					int d = 0;
					if(curX != -1 && curY != -1 && selX != -1 && selY != -1)
					{
						a = dirX[curX][curY];
						b = dirY[curX][curY];
						c = dirX[selX][selY];
						d = dirY[selX][selY];
					}
					if( Marquer_Group() != 0)
					{
						//supprimer le ou les groupe restant
//						System.out.println("plus d'un groupe dans la grille");
						mult++;
						if (mult > 1){
							timing += mult;	//rajoute du temps bonus
						}
						//passer en clignotement des groupes
						gameState = MODE_BLINKING;
						sens = -1;
						compteur = 0;
					}else{
						//plus de groupe a supprimer
						if( sens == 0){
							//les joyaux doivent reprendre leur état initiale
//							System.out.println("change le sens d'inversion des joyaux");
							sens = 1 - sens;	///passe de 0 à 1 ou de 1 à 0

							dirX[curX][curY] = a;
							dirY[curX][curY] = b;

							dirX[selX][selY] = c;
							dirY[selX][selY] = d;

							compteur = 0;	//lancer tout de suite l'animation d'inversion
							gameState = MODE_EXCHANGE;
						}else{
//							System.out.println("fin animation inversion");
							sens = -1;
							raz_Selection();
							compteur = 0;
							gameState = MODE_NORMAL;
						}
					}
					break;

				case MODE_BLINKING:	//faire clignoter 4 fois
					raz_Selection();
					sens = -1;
					pause(anims);
					compteur++;
					if ( compteur >= anims)
					{
						int k = delete_Jewel();	//supprimer les grappes de 3 et +
//						System.out.println("suprimer les grappes restants "+k);
						score += 10 * mult * k;//garder le score de la série
						//ejb.UpdateScore(score);
						tomber_Jewel();				//faire tomber la première série de joyaux
						gameState = MODE_FALLING;	//passer en clignotement des groupes
						compteur = 0;
					}
					break;

				case MODE_FALLING:	//faire tomber les joyaux vers le bas
					//l'animation de la chute d'un cran de joyau est terminée

					compteur++;
					if ( compteur >= anims)
					{
						//décaler une rangée de joyau vers le bas
						decaler_Jewel();
						int n = compter_Delete();	//regarde s'il reste des cases vides
						if(n < 1){
							//plus de case vide
							if (mult > 1){
								mult = 1;	//rajoute du temps bonus
								bonus += mult;
								//modifier la barre du bonus
								if( bonus >= 20){
									//efface des joyaux aléatoirement
									for(int k=0; k<bonus; k++){
										int i =  (int)(Math.random() * nbX);
										int j =  (int)(Math.random() * nbX);
										stat[i][j] = -1;
									}
									bonus = 0;
//									System.out.println("*");
//									System.out.println("GAIN BONUS");
									//efface
									gameState = MODE_BLINKING;
								}else{
									gameState = MODE_DELETED;
								}
							}else{
								gameState = MODE_DELETED;
							}
						}else{
							tomber_Jewel();
						}
						compteur = 0;
					}
					pause(anims);
					break;
				case MODE_LOOSE:
					//ecran finit
					//
					pause(anims);
					compteur++;
					timing += compteur * 2 * sens - sens * 10;
					if ( compteur >= anims)
					{
						//Petit defilement du texte
						if ( sens == 1)
						{
							sens = - 1;
						}else{
							sens = 1;
						}
						compteur = 0;
					}
					
			}
			
			repaint();
        }
    }

	// ----------------------------------------------------------------------------
  public void start() {
    if (runner == null); {
        runner = new Thread(this);
        runner.start();
    }
  }
	// ----------------------------------------------------------------------------
  public void pause(int time) {
      try {
            Thread.sleep(time);
      } catch (InterruptedException e) { }
//      System.out.println("pause "+time);
  }
	// ----------------------------------------------------------------------------
  public void update(Graphics screen) {
        paint(screen);
  }

@Override
public void mouseClicked(MouseEvent e) {
	// TODO Auto-generated method stub
	
}

@Override
public void mousePressed(MouseEvent e) {
	// TODO Auto-generated method stub
	int x = e.getX();
    int y = e.getY();
	int k = 1;
	k = (int)Math.floor(32 / anims);
	if(x > 32 && y > 32 && x <= (nbX+1) * 32 && y <= (nbY+1) * 32 ){
		//clic dans le cadre de jeu
		if(gameState == 0)
		{
			//on ne peut selectionné les joyaux qu'en etat normal
			if (curX == -1)
			{
				//nouveau clic
				curX = (x-32)/32;
				curY = (y-32)/32;
			}else{
				//un premier joyau a déja été selectionné
				//selectionner le deuxieme près du premier
				selX = (x-32)/32;
				selY = (y-32)/32;
				if (selX == curX && selY == (curY-1))
				{
					dirY[curX][curY] = -k ;
					dirY[selX][selY] = k;
					sens = 0;
					compteur = 0;
//					System.out.println("selection des joyaux "+curX+","+curY+" - "+selX+","+selY);
					gameState = MODE_EXCHANGE;	//inverser les 2 joyaux
				}
				else if (selX == curX && selY == (curY+1))
				{
					dirY[curX][curY] = k;
					dirY[selX][selY] = -k;
					sens = 0;
//					System.out.println("selection des joyaux "+curX+","+curY+" - "+selX+","+selY);
					compteur = 0;
					gameState = MODE_EXCHANGE;	//inverser les  2 joyaux
				}
				else if (selY == curY && selX == (curX-1))
				{
					dirX[curX][curY] = -k;
					dirX[selX][selY] = k;
					sens = 0;
//					System.out.println("selection des joyaux "+curX+","+curY+" - "+selX+","+selY);
					compteur = 0;
					gameState = MODE_EXCHANGE;	//inverser les  2 joyaux
				}
				else if (selY == curY && selX == (curX+1))
				{
					dirX[curX][curY] = k;
					dirX[selX][selY] = -k;
					sens = 0;
//					System.out.println("selection des joyaux "+curX+","+curY+" - "+selX+","+selY);
					compteur = 0;
					gameState = MODE_EXCHANGE;	//inverser les  2 joyaux
				}
				else
				{
					//les deux joyaux sont pas adjacents : les deselectionner
					raz_Selection();
					sens = -1;
				}
			}
		}
		repaint();
	}
}

@Override
public void mouseReleased(MouseEvent e) {
	// TODO Auto-generated method stub
	
}

@Override
public void mouseEntered(MouseEvent e) {
	// TODO Auto-generated method stub
	
}

@Override
public void mouseExited(MouseEvent e) {
	// TODO Auto-generated method stub
	
}

}
