package main;
class Info {
	static int	RAICES	= 1;
	static int	DERIVAR	= 2;
	//---------------------------------------------------------------------------
	private	String	message;
	private	int		position;
	private	char	character;
	private	boolean	execute;
	//---------------------------------------------------------------------------
	public Info(String message,int position,char character) {
		this.message	= message;
		this.position	= position;
		this.character	= character;
		execute			= false;
	}
	//---------------------------------------------------------------------------
	public String getMessage() {
		return message;
	}
	//---------------------------------------------------------------------------
	public int getPosition() {
		return position;
	}
	//---------------------------------------------------------------------------
	public char getCharacter() {
		return character;
	}
	//---------------------------------------------------------------------------
	public boolean execute() {
		return execute;
	}	
}

class Automata {
	
	//---------------------------------------------------------------------------

	//								  #  +-   .   x   ^   :   ,   _   i   t   D
	
	private	int		gra[][]	=	{	{ 2 , 1 , 0 , 5 , 0 , 0 , 0 ,14 , 0 , 0 , 0 },	// 0
									{ 2 , 0 , 0 , 5 , 0 , 0 , 0 ,15 , 0 , 0 , 0 },	// 1
									{ 2 , 1 , 3 , 5 , 0 , 8 , 0 ,16 , 0 , 0 , 0 },	// 2
									{ 4 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 },	// 3
									{ 4 , 1 , 0 , 5 , 0 , 8 , 0 ,16 , 0 , 0 , 0 },	// 4
									{ 0 , 1 , 0 , 0 , 6 , 8 , 0 ,16 , 0 , 0 , 0 },	// 5
									{ 7 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 },	// 6
									{ 7 , 1 , 0 , 0 , 0 , 8 , 0 ,16 , 0 , 0 , 0 },	// 7

									//parametros para metodo de la bisecci�n
									{10 , 9 , 0 , 0 , 0 , 0 , 0 ,18 , 0 , 0 , 0 },	// 8
									{10 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 },	// 9
									{10 , 0 ,11 , 0 , 0 , 0 ,13 ,17 ,19 ,20 , 0 },	// 10
									{12 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 },	// 11
									{12 , 0 , 0 , 0 , 0 , 0 ,13 ,17 , 0 ,20 , 0 },	// 12
									{10 , 9 , 0 , 0 , 0 , 0 , 0 ,18 , 0 , 0 , 0 },	// 13
									
									//espacios en blanco
									{ 2 , 1 , 0 , 5 , 0 , 0 , 0 ,14 , 0 , 0 , 0 },	// 14
									{ 2 , 0 , 0 , 5 , 0 , 0 , 0 ,15 , 0 , 0 , 0 },	// 15
									{ 0 , 1 , 0 , 0 , 0 , 8 , 0 ,16 , 0 , 0 , 0 },	// 16
									{ 0 , 0 , 0 , 0 , 0 , 0 ,13 ,17 , 0 , 0 , 0 },	// 17
									{10 , 9 , 0 , 0 , 0 , 0 , 0 ,18 , 0 , 0 , 0 },	// 18
									{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 },	// 19
									{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 },	// 20
									
									//derivar
									{ 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 }	// 21
								};
												 	
	private	String	voc[]	=	{	"1234567890",
									"+-",
									".",
									"Xx",
									"^e",
									":",
									",",
									" ",
									"iI",
									"tT",
									"D"};
	
	private	int		ace[]	=	{	2,4,5,7,16,17,19,20,21	};
	
	private	int		est		=	0;
	
	private	String	mes[]	=	{	"",
									"Unknow symbol",
									"Other symbol is expected",
									"Incomplete expression",
									"Terms exponents must be differents",
									"Arguments missing",
									"Parameters insufficient"};

	//---------------------------------------------------------------------------
	
	public Automata() {
	}
	
	//---------------------------------------------------------------------------

	public void setMatrix(int gra[][],String voc[],int ace[]){
		this.gra	= gra;
		this.voc	= voc;
		this.ace	= ace;
	}
	
	//---------------------------------------------------------------------------

	public Info validate(String text) {
		int		tip,i;
		char 	c	=' ';
		
		est	= 0;
		
		for (i=0;i<text.length();i++) {
			c = text.charAt(i);

			tip = getType(c);
			
			if (tip<0)	return new Info(mes[1],i,c);
			
			est = gra[est][tip];
			
			if (est==0)	return new Info(mes[2],i,c);
		}
		
		for (int j=0;j<ace.length;j++) {
			if (ace[j]==est) {
				return new Info(mes[0],i,' ');				
			}
		}
		
		return new Info(mes[3],i,' ');
	}
	
	//---------------------------------------------------------------------------
	
	private int getType(char character) {
		for (int i=0;i<voc.length;i++) {
			for (int c=0;c<voc[i].length();c++)
				if (voc[i].charAt(c)==character) return i;
		}
		return -1;
	}
}