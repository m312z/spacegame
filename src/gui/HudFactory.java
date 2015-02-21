package gui;

import static core.Frame.SCREEN_SIZE;
import gui.FontManager.FontType;
import gui.ui.ButtonElement;
import gui.ui.HudElement;
import gui.ui.HudElement.InteractionType;
import gui.ui.HudOverlay;
import gui.ui.PanelElement;
import gui.ui.TextElement;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import model.Board;
import model.Player.PlayerType;
import model.building.Building;
import model.building.BuildingType;
import phys.Point2D;
import phys.Shape;

/**
 * This is the GUI factory that generates the HUD and popups that comprise the user interfaces.
 * @author michael
 *
 */
public class HudFactory {
	
	public static HudOverlay makeHUD(PlayerType playerType) {
		
		switch(playerType) {
		case BUILDER:
			return makeBuilderOverlay();
		default:
			return new HudOverlay();
		}
	}
	
	/**
	 * @return the HudOverlay used by the builder player. 
	 */
	private static HudOverlay makeBuilderOverlay() {
				
		HudOverlay builderOverlay = new HudOverlay();
		float hs = SCREEN_SIZE[1]/32;
		float eps = 2;//hs/16f; 
				
		Shape bs = new Shape(new Point2D[] {
				new Point2D(-hs,-hs), new Point2D(hs,-hs),
				new Point2D(hs,hs),new Point2D(-hs,hs)
		});
		
		Shape tps = new Shape(new Point2D[] {
				new Point2D(-SCREEN_SIZE[0]/2,-hs/2), new Point2D(SCREEN_SIZE[0]/2,-hs/2),
				new Point2D(SCREEN_SIZE[0]/2,hs/2),new Point2D(-SCREEN_SIZE[0]/2,hs/2)
		});

		PanelElement topPanel = new PanelElement("top_panel_back", tps, new Point2D(SCREEN_SIZE[0]/2,-tps.getMinY()), Color.BLACK, Pallete.unset);
		builderOverlay.addElement(topPanel);
		
		int i = 0;
//		for(GameCategory gc: GameCategory.values()) {
			
			ButtonElement buildButton = new ButtonElement("buildButton", bs, new Point2D(hs,(2*hs+eps)*(i+1)), Color.BLACK, Pallete.unset, "buttonImage", "buttonImage");
			buildButton.addCommand(InteractionType.MOUSE_DOWN, "showBuild");
			builderOverlay.addElement(buildButton);
			int j=0;
			for(BuildingType type: BuildingType.values()) {
				
//				if(type.category!=gc) continue;
				
				// button
				buildButton = new ButtonElement("buttonBuild_"+type.name(),bs, new Point2D(hs*3 + eps,(2*hs+eps)*(j+1)), Color.BLACK, Pallete.unset, type.name(), type.name());
				buildButton.addCommand(InteractionType.MOUSE_DOWN, "build/"+type.name());
				buildButton.addCommand(InteractionType.MOUSE_OVER, "showTooltip/"+type.name());
				buildButton.addCommand(InteractionType.MOUSE_OFF, "hideTooltip/"+type.name());
				if(type.equals("")) {
					TextElement buttonText = new TextElement("buildButtonText",bs, new Point2D(0,0), type.name(), FontType.FONT_24);
					buildButton.addElement(buttonText);
				}
				buildButton.setVisible(false);
				builderOverlay.addElement(buildButton);
				
				// tool-tip
				HudElement btt = makeBuildingToolTip(type);
				btt.getPos().x = 2*(hs*2 + eps);
				btt.getPos().y = (2*hs+eps)*(j+1) - hs;
				btt.setName("toolTip_"+type.name());
				btt.setVisible(false);
				builderOverlay.addElement(btt);
				
				j++;
			}
			i++;
//		}
		
		ButtonElement destroyButton = new ButtonElement("removeButton", bs, new Point2D(hs,(2*hs+eps)*(i+1)), Color.BLACK, Color.ORANGE, "buttonImage", "buttonImage");
		destroyButton.addCommand(InteractionType.MOUSE_DOWN, "remove");
		builderOverlay.addElement(destroyButton);
			
		ButtonElement gooButton = new ButtonElement("gooButton", bs, new Point2D(hs,(2*hs+eps)*(i+2)), Color.BLACK, Color.GREEN, "buttonImage", "buttonImage");
		gooButton.addCommand(InteractionType.MOUSE_DOWN, "goo");
		builderOverlay.addElement(gooButton);
		
		return builderOverlay;
	}
	
	public static HudElement makeBuildingToolTip(BuildingType type) {
		
		FontType fontType = FontType.FONT_24;
		TrueTypeFont font = FontManager.getFont(fontType);
		
		String name = type.name();
		float th = font.getHeight();
		float hw = font.getWidth(name);
		float hh = th;
		
		List<String> lines = new ArrayList<String>();
				
		for(String s: lines) {
			if(font.getWidth(s) > hw)
				hw = font.getWidth(s);
		}
		
		hw += th;
		hh = (lines.size()+1)*th;
		
		Shape tls = new Shape(new Point2D[] {
				new Point2D(0,0), new Point2D(hw,0),
				new Point2D(hw,th),new Point2D(0,th)
		});
		Shape bs = new Shape(new Point2D[] {
				new Point2D(0,0), new Point2D(hw,0),
				new Point2D(hw,hh),new Point2D(0,hh)
		});
		
		PanelElement tip = new PanelElement("tooltip", bs, new Point2D(), Color.BLACK, Pallete.unset);
		
		TextElement tipText = new TextElement("tooltip1", tls, new Point2D(0,0), name, fontType);
		tip.addElement(tipText);
		
		for(int i=0;i<lines.size();i++) {
			tipText = new TextElement("tooltip1", tls, new Point2D(0,th*(i+1)), lines.get(i), fontType);
			tip.addElement(tipText);	
		}
		
		return tip;
	}
	
	public static HudElement makeBuildingToolTip(Board board, float x, float y, Building building) {
		
		FontType fontType = FontType.FONT_24;
		TrueTypeFont font = FontManager.getFont(fontType);
		
		String name = building.getTileType().name();
		float th = font.getHeight();
		float hw = font.getWidth(name);
		float hh = th;
		
		List<String> lines = new ArrayList<String>();
						
		for(String s: lines) {
			if(font.getWidth(s) > hw)
				hw = font.getWidth(s);
		}
		
		hw = hw + th;
		hh = (lines.size()+1)*th;
				
		Shape tls = new Shape(new Point2D[] {
				new Point2D(-hw/2f,-th/2f), new Point2D(hw/2f,-th/2f),
				new Point2D(hw/2f,th/2f),new Point2D(-hw/2f,th/2f)
		});
		Shape bs = new Shape(new Point2D[] {
				new Point2D(-hw/2f,0), new Point2D(hw/2f,0),
				new Point2D(hw/2f,hh),new Point2D(-hw/2f,hh)
		});
		
		PanelElement tip = new PanelElement("tooltip", bs, new Point2D(x,y-hh), Color.BLACK, Pallete.unset);
		
		TextElement tipText = new TextElement("tooltip1", tls, new Point2D(0,th/2f), name, fontType);
		tip.addElement(tipText);
		
		for(int i=0;i<lines.size();i++) {
			tipText = new TextElement("tooltip1", tls, new Point2D(0,th*(i+3/2f)), lines.get(i), fontType);
			tip.addElement(tipText);	
		}
		
		return tip;
	}	
}
