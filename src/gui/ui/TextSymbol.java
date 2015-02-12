package gui.ui;


public enum TextSymbol
{
	GRANTS("[GRANTS]","grants",64),
	BONUS("[BONUS]","bonus",64),
	NEUTRAL("[NEUTRAL]","neutral",32),
	HOUSING("[HOUSING]","housing",32),
	SCIENCE("[SCIENCE]","science",32),
	MATERIAL("[MATERIAL]","material",32),
	POWER("[POWER]","power",32),
	MILITARY("[MILITARY]","military",32);
	
	public String text;
	public String image;
	public int width;
	
	private TextSymbol(String text, String image, int width) {
		this.text = text;
		this.image = image;
		this.width = width;
	}
}