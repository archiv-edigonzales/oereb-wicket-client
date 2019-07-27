package ch.so.agi.oereb.wicketclient.wicket;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.wicketstuff.openlayers3.DefaultOpenLayersMap;
import org.wicketstuff.openlayers3.api.Extent;
import org.wicketstuff.openlayers3.api.Map;
import org.wicketstuff.openlayers3.api.View;
import org.wicketstuff.openlayers3.api.coordinate.Coordinate;
import org.wicketstuff.openlayers3.api.coordinate.LongLat;
import org.wicketstuff.openlayers3.api.layer.Layer;
import org.wicketstuff.openlayers3.api.layer.Tile;
import org.wicketstuff.openlayers3.api.source.tile.Osm;
import org.wicketstuff.openlayers3.api.source.tile.TileWms;

import com.google.common.collect.ImmutableMap;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.DocumentBaseType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.DocumentType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ExtractType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RestrictionOnLandownershipType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ThemeType;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.IeEdgeMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MobileViewportMetaTag;

public class ExtractPage extends WebPage {
    private static final long serialVersionUID = 1L;
    
    GetExtractByIdResponse extractResponse;
    
    @SuppressWarnings("unchecked")
    public ExtractPage(GetExtractByIdResponse extractResponse) throws UnsupportedEncodingException {
        this.extractResponse = extractResponse;
        
        add(new HtmlTag("html"));
        MobileViewportMetaTag mvt = new MobileViewportMetaTag("viewport");
        mvt.setWidth("device-width");
        mvt.setInitialScale("1");
        add(mvt);
        add(new IeEdgeMetaTag("ie-edge"));
        add(new MetaTag("description", Model.of("description"), Model.of("OEREB...")));
        add(new MetaTag("author", Model.of("author"), Model.of("Stefan Ziegler <stefan.ziegler.de@gmail.com>")));

        ExtractType extractType = extractResponse.getValue().getExtract().getValue();

        ExternalImage cantonalLogoImage = new ExternalImage("cantonalLogo", new Model(extractType.getCantonalLogoRef()));
        cantonalLogoImage.add(new AttributeModifier("height", "50"));
        add(cantonalLogoImage);
        
        ExternalImage municipalityLogoImage = new ExternalImage("municipalityLogo", new Model(extractType.getMunicipalityLogoRef()));
        municipalityLogoImage.add(new AttributeModifier("height", "50"));
        add(municipalityLogoImage);
        
        add(new Label("realEstateNumber", Model.of(extractType.getRealEstate().getNumber())));
        add(new Label("egrid", Model.of(extractType.getRealEstate().getEGRID())));
        add(new Label("municipality", Model.of(extractType.getRealEstate().getMunicipality())));
        
        
        List<ThemeType> concernedThemes = extractType.getConcernedTheme();
        ListView concernedThemesListView = new ListView("concernedThemes", concernedThemes) {
            @Override
            protected void populateItem(ListItem item) {
                ThemeType theme = (ThemeType) item.getModelObject();
                item.add(new Label("themeText", theme.getText().getText()));
                item.add(new Label("themeCode", theme.getCode()));
            }
        };
        add(concernedThemesListView);
        
        List<ThemeType> notConcernedThemes = extractType.getNotConcernedTheme();
        ListView notConcernedThemesListView = new ListView("notConcernedThemes", notConcernedThemes) {
            @Override
            protected void populateItem(ListItem item) {
                ThemeType theme = (ThemeType) item.getModelObject();
                item.add(new Label("themeText", theme.getText().getText()));
                item.add(new Label("themeCode", theme.getCode()));
            }
        };
        add(notConcernedThemesListView);

        List<ThemeType> themesWithoutData = extractType.getThemeWithoutData();
        ListView themesWithoutDataListView = new ListView("themesWithoutData", themesWithoutData) {
            @Override
            protected void populateItem(ListItem item) {
                ThemeType theme = (ThemeType) item.getModelObject();
                item.add(new Label("themeText", theme.getText().getText()));
                item.add(new Label("themeCode", theme.getCode()));
            }
        };
        add(themesWithoutDataListView);
        
        List<RestrictionOnLandownershipType> restrictionsOnLandownership = extractType.getRealEstate().getRestrictionOnLandownership();
        ListView restrictionsOnLandownershipListView = new ListView("restrictionsOnLandownership", restrictionsOnLandownership) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(ListItem item) {
                RestrictionOnLandownershipType restrictionOnLandownership = (RestrictionOnLandownershipType) item.getModelObject();
                item.add(new Label("themeText", restrictionOnLandownership.getTheme().getText().getText()));
                item.add(new Label("plrInformation", restrictionOnLandownership.getInformation().getLocalisedText().get(0).getText()));
                
                ExternalImage symbolImage;
                try {
                    symbolImage = new ExternalImage("plrSymbol", new Model(java.net.URLDecoder.decode( restrictionOnLandownership.getSymbolRef().trim(), "UTF-8")));
                    symbolImage.add(new AttributeModifier("height", "20"));
                    item.add(symbolImage);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    error(e.getMessage());
                }
                
                
                List<DocumentBaseType> legalProvisions = restrictionOnLandownership.getLegalProvisions();
                ListView<DocumentBaseType> legalProvisionsListView = new ListView<DocumentBaseType>("legalProvisions", legalProvisions) {
                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void populateItem(ListItem<DocumentBaseType> item) {
                        DocumentType documentType = (DocumentType) item.getModelObject();
//                        item.add(new Label("legalProvisionTitle", documentType.getTitle().getLocalisedText().get(0).getText()));
                        
                        try {
                            ExternalLink externalLink = new ExternalLink("legalProvisionLink", 
                                    java.net.URLDecoder.decode(documentType.getTextAtWeb().getLocalisedText().get(0).getText().trim(), "UTF-8"), 
                                    documentType.getTitle().getLocalisedText().get(0).getText());
                            externalLink.add(new AttributeModifier("target", "_blank"));
                            item.add(externalLink);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                            error(e.getMessage());
                        }

                    }
                };
                item.add(legalProvisionsListView);
                
//                List<DocumentBaseType> legalProvisions = restrictionOnLandownership.getLegalProvisions();
//                Iterator<DocumentBaseType> it = legalProvisions.iterator();
//                while(it.hasNext()) {
//                    DocumentType documentType = (DocumentType) it.next();
//                    System.out.println(documentType.getDocumentType());
//                    System.out.println(documentType.getTextAtWeb().getLocalisedText().get(0).getText());
//                    System.out.println(documentType.getTitle().getLocalisedText().get(0).getText());  
//                }
            }
        };
        add(restrictionsOnLandownershipListView);

        
        
        
        
        System.out.println(java.net.URLDecoder.decode(new String(extractType.getRealEstate().getPlanForLandRegisterMainPage().getReferenceWMS()).trim(), "UTF-8"));

        System.out.println(extractType.getRealEstate().getRestrictionOnLandownership().get(0).getTheme().getText().getText());
        System.out.println(extractType.getRealEstate().getRestrictionOnLandownership().get(0).getInformation().getLocalisedText().get(0).getText());
        System.out.println(extractType.getRealEstate().getRestrictionOnLandownership().get(0).getMap().getReferenceWMS());
        
        String url = extractType.getRealEstate().getRestrictionOnLandownership().get(0).getMap().getReferenceWMS();
        String decodedUrl = java.net.URLDecoder.decode(url.trim(), "UTF-8");
        System.out.println(decodedUrl);
        
        add(new DefaultOpenLayersMap("map",
                // create the model for our map
                Model.of(new Map(
                        // list of layers
                        Arrays.<Layer>asList(
                                // a new tile layer with the map of the world
                                new Tile("Open Street Maps", new Osm()),
                                new Tile("USA", new TileWms("https://ahocevar.com/geoserver/wms",
                                        ImmutableMap.of("LAYERS", "topp:states", "VERSION", "1.1.1"))),
                                new Tile("Grunbuchplan", new TileWms("https://geowms.bl.ch/",
                                        ImmutableMap.of("LAYERS", "grundbuchplan_group", "VERSION", "1.1.1")))
                                ),
                        // view for this map
                        new View(new Coordinate(847738.6,6022968.6), 10)))));
        
        
//        Extent extent = new Extent();
//        extent.setMinimum(new LongLat(new Coordinate(2613003.00046,1260353.56221), "EPSG:2056"));
//        extent.setMaximum(new LongLat(new Coordinate(2613062.83054,1260387.54279), "EPSG:2056"));

//        add(new DefaultOpenLayersMap("map",
//                Model.of(new Map(
//                        Arrays.<Layer>asList(
//                                new Tile("Grundbuchplan", new TileWms("https://geowms.bl.ch/",
//                                        ImmutableMap.of("LAYERS", "grundbuchplan_group", "VERSION", "1.1.1")))
//                                ),
//                        new View(new Coordinate(2613030, 1260365), 2, 10, "EPSG:2056", extent)))));
//
////        Extent extent = new Extent();
////        extent.setMinimum(new LongLat(new Coordinate(2613003.00046,1260353.56221), "EPSG:2056"));
////        extent.setMaximum(new LongLat(new Coordinate(2613062.83054,1260387.54279), "EPSG:2056"));
//        View view = new View(new Coordinate(2613030, 1260365), 2, 10, "EPSG:2056", extent);
//        System.out.println(view.renderJs());

        
    }        

    // 2613003.00046 1260353.56221 2613062.83054 1260387.54279
    
}
