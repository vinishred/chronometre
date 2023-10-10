package Chronometre;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

//la classe JFrame est une fenêtre
public class Chrono extends JFrame{
	private JPanel container = new JPanel();
	//Tableau contenant les éléments à afficher dans le chrono
	String[] tab_string = { "Start", "Lap", "Stop", "Resume", "Reset" };
	//Uh bouton par élément à afficher
	JButton[] tab_button = new JButton[tab_string.length];
	// 4 labels à afficher, un pour le chrono principal et 3 pour les competurs de tours
	private JLabel[] ecran = new JLabel[4];
	//Une variable pour stocker la dimension de chaque bouton du tableau
	private Dimension dim = new Dimension(90, 70);
	//Une variable pour connaitre le nombre de tours affichés (on rappelle max 3), on l'initialise à 0
	private int lap = 1;
	//des vraiables pour calculer le temps et simuler le chrono qu'on initiamlise à 0
	long initVar, nowVar, pauseDepart, pauseFin = 0;
	//les variables qui vont formater le temps
	long hour, minute, second, milli;
	//le SwingWorker qui va permettre de lancer le chrono en background
	//pour ne pas bloquer l'application et ne pas geler l'interface utilisateur
	private SwingWorker<Void, Integer> worker;
	
	public Chrono() {
		//définit la taille de la fenêtre
		this.setSize(350, 435);
		//définit le titre de la fenêtre
		this.setTitle("ChronoJava");
		//permet de fermer la fenetre en cliquant sur la croix
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//permet de ne pas positionner la fenetre dans l'angle sup gauche
		this.setLocationRelativeTo(null);
		//permet de ne pas redimensionner la fenêtre
		this.setResizable(false);
		
		//on initialise le conteneur avec tous les composants
		initComposant();
		//On ajoute le conteneur à la fenêtre
		this.setContentPane(container);
		//et on le rend visible
		this.setVisible(true);
	}
	
	private void initComposant() {
		//On définit les deux sous containers 
		//un qui va contenir les labels d'affichage du temps et un avec les boutons d'actions
		JPanel buttons = new JPanel();
		buttons.setPreferredSize(new Dimension(320, 225));
		JPanel panEcran = new JPanel();
		panEcran.setPreferredSize(new Dimension(320, 230));
		panEcran.setBorder(BorderFactory.createLineBorder(Color.black, 2));
		panEcran.setBackground(Color.white);
		
		//On définit la police d'écriture à utiliser
		Font police = new Font("Arial", Font.BOLD, 50);
		Font police2 = new Font("Arial", Font.BOLD, 25);
		
		//On initialise les JLables avec le texte de départ, la police 
		//et la dimension et on ajoute les sous conteneurs corespondants
		for(int i = 0; i < 4; i++) {
			if(i == 0) {
				ecran[i] = new JLabel("00:00:00.00");
				ecran[i].setFont(police);
			} else {
				ecran[i] = new JLabel(i + " : 00:00:00.00");
				ecran[i].setFont(police2);
			}
		//on aligne les informations au centre dans le JLabel
			ecran[i].setHorizontalAlignment(JLabel.CENTER);
			ecran[i].setPreferredSize(new Dimension(300, 50));
			panEcran.add(ecran[i]);
		}
		
		//On parcourt le tableau initialisé afin de créer nos boutons 
		//avec le texte, la dimension et on ajoute au sous conteneur
		for(int i = 0; i < tab_string.length; i++) {
			tab_button[i] = new JButton(tab_string[i]);
			tab_button[i].setPreferredSize(dim);
			buttons.add(tab_button[i]);
			//on départ tous les bouttons sauf celui à l'indice 0, ici le start
			if(i != 0) {
				tab_button[i].setEnabled(false);
			}
		}
		
		//On définit pour chaque bouton un listener et une couleur
		tab_button[0].addActionListener(new StartListener());
		tab_button[0].setForeground(Color.RED);
		tab_button[1].addActionListener(new LapListener());
		tab_button[1].setForeground(Color.MAGENTA);
		tab_button[2].addActionListener(new StopListener());
		tab_button[2].setForeground(Color.BLUE);
		tab_button[3].addActionListener(new ResumeListener());
		tab_button[3].setForeground(Color.RED);
		tab_button[4].addActionListener(new ResetListener());
		tab_button[4].setForeground(Color.GREEN);
		
		//On ajoute après les deux sous conteneurs au conteneur principal
		container.add(panEcran, BorderLayout.NORTH);
		container.add(buttons, BorderLayout.CENTER);
	}
	
	private void initializeWorker() {
		worker = new SwingWorker<Void, Integer>() {
			protected Void doInBackground() throws Exception {
				
				initVar = initVar + (pauseFin - pauseDepart);
				pauseDepart = pauseFin = 0;
				
				while (!isCancelled()) {
					nowVar = System.currentTimeMillis() - initVar;
					nowVar /= 10;
					milli  = nowVar % 100;
					second = nowVar / 100;
					minute = second / 60;
					second = second % 60;
					hour = minute / 60;
					minute = minute % 60;
					ecran[0].setText(String.format("%02d", hour) 
							+ ":" + String.format("%02d", minute) 
							+ ":" + String.format("%02d", second) 
							+ "." + String.format("%02d", milli));
				}
				return null;
			}
		};
	}
	
	//Listener affecté au bouton start
	class StartListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			initVar = System.currentTimeMillis();
			initializeWorker();
			worker.execute();
			tab_button[0].setEnabled(false);
			tab_button[1].setEnabled(true);
			tab_button[2].setEnabled(true);
			tab_button[3].setEnabled(true);
			tab_button[4].setEnabled(true);
		}
	}
	
	//Listener affecté au bouton lap
	class LapListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			if(lap == 3) {
				tab_button[1].setEnabled(false);				
			}
			ecran[lap].setText(String.format("%02d", hour) 
					+ ":" + String.format("%02d", minute) 
					+ ":" + String.format("%02d", second) 
					+ "." + String.format("%02d", milli));
			lap++;
		}
	}
	
	//Listener affecté au bouton stop
		class StopListener implements ActionListener {
			public void actionPerformed(ActionEvent arg0) {
				worker.cancel(true);
				pauseDepart = System.currentTimeMillis();
				tab_button[2].setEnabled(false);
				tab_button[3].setEnabled(true);
			}
		}
		
	//Listener affecté au bouton Resume
		class ResumeListener implements ActionListener {
			public void actionPerformed(ActionEvent arg0) {
				pauseFin = System.currentTimeMillis();
				initializeWorker();
				worker.execute();
				tab_button[2].setEnabled(true);
				tab_button[3].setEnabled(false);
			}
		}
		
	//Listener affecté au bouton Reset
		class ResetListener implements ActionListener {
			public void actionPerformed(ActionEvent arg0) {
				worker.cancel(true);
				hour = minute = second = milli = pauseDepart = pauseFin = 0;
				lap = 0;
				for(int i = 0; i < 4; i++) {
					if(i == 0) {
						ecran[i].setText("00:00:00.00");
					} else {
						ecran[i].setText(i + " : 00:00:00.00");
					}
				}
				tab_button[0].setEnabled(true);
				tab_button[1].setEnabled(false);
				tab_button[2].setEnabled(false);
				tab_button[3].setEnabled(false);
				tab_button[4].setEnabled(false);
			}
		}
}
