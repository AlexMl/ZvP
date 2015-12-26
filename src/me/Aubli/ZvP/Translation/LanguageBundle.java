package me.Aubli.ZvP.Translation;

import java.util.ListResourceBundle;
import java.util.Locale;

import me.Aubli.ZvP.Translation.Resources.DefaultTranslation;
import me.Aubli.ZvP.Translation.Resources.GermanTranslation;
import me.Aubli.ZvP.Translation.Resources.HungarianTranslation;
import me.Aubli.ZvP.Translation.Resources.SpanishTranslation;


public abstract class LanguageBundle extends ListResourceBundle {
    
    private enum Bundle {
	ENGLISH("me.Aubli.ZvP.Translation.Resources.DefaultTranslation"),
	GERMAN("me.Aubli.ZvP.Translation.Resources.GermanTranslation"),
	HUNGARIAN("me.Aubli.ZvP.Translation.Resources.HungarianTranslation"),
	SPANISH("me.Aubli.ZvP.Translation.Resources.SpanishTranslation"), ;
	
	private String baseName;
	
	private Bundle(String baseName) {
	    this.baseName = baseName;
	}
	
	private LanguageBundle getInstance() {
	    switch (this) {
		case ENGLISH:
		    return new DefaultTranslation();
		case GERMAN:
		    return new GermanTranslation();
		case HUNGARIAN:
		    return new HungarianTranslation();
		case SPANISH:
		    return new SpanishTranslation();
		    
		default:
		    throw new IllegalArgumentException();
	    }
	}
	
	private static Bundle getBundle(String baseName) {
	    for (Bundle bundle : values()) {
		if (bundle.baseName.equals(baseName)) {
		    return bundle;
		}
	    }
	    return null;
	}
    }
    
    @Override
    public abstract Locale getLocale();
    
    public abstract String getAuthor();
    
    @Override
    protected abstract Object[][] getContents();
    
    public static LanguageBundle[] getLanguageBundles() {
	LanguageBundle[] bundles = new LanguageBundle[Bundle.values().length];
	
	for (int i = 0; i < Bundle.values().length; i++) {
	    bundles[i] = Bundle.values()[i].getInstance();
	}
	return bundles;
    }
    
    public static LanguageBundle getLanguageBundle(String baseName) {
	return Bundle.getBundle(baseName).getInstance();
    }
}
