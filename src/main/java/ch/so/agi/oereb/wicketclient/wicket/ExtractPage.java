package ch.so.agi.oereb.wicketclient.wicket;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ExternalImage;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.DocumentBaseType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.DocumentType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ExtractType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RestrictionOnLandownershipType;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ThemeType;
import ch.so.agi.oereb.wicketclient.openlayers3.api.layer.ImageLayer;
import ch.so.agi.oereb.wicketclient.openlayers3.api.source.single.ImageWms;
import ch.so.agi.oereb.wicketclient.projection.ApproxSwissProj;
import ch.so.agi.oereb.wicketclient.projection.SphericalMercator;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.IeEdgeMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MobileViewportMetaTag;

public class ExtractPage extends WebPage {
    private static final long serialVersionUID = 1L;
    
    GetExtractByIdResponse extractResponse;
    
    @SuppressWarnings("unchecked")
    public ExtractPage(GetExtractByIdResponse extractResponse) throws UnsupportedEncodingException, URISyntaxException, MalformedURLException {
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
                
        // TODO: Gruppierung der ÖREB.
        // Inklusive dazugehöriger Dokumente (Rechtsvorschriften, Gesetze, ...)
        
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
//                    
//                    documentType.getReference()
//                }
            }
        };
        add(restrictionsOnLandownershipListView);

        // Add WMS layers to map.
        List<Layer> layerList = new ArrayList<Layer>();

        Iterator<RestrictionOnLandownershipType> it = restrictionsOnLandownership.iterator();
        while(it.hasNext()) {
            RestrictionOnLandownershipType restrictionOnLandownership = it.next();
            
            String restrictionOnLandownershipWmsUrl = java.net.URLDecoder.decode(new String(restrictionOnLandownership.getMap().getReferenceWMS()).trim(), "UTF-8");
            String restrictionOnLandownershipWmsLayers = null;
            
            List<NameValuePair> restrictionOnLandownershipWmsUrlParams = URLEncodedUtils.parse(new URI(restrictionOnLandownershipWmsUrl), Charset.forName("UTF-8"));
            for (NameValuePair param : restrictionOnLandownershipWmsUrlParams) {
                if (param.getName().equalsIgnoreCase("LAYERS")) {
                    restrictionOnLandownershipWmsLayers = param.getValue();
                    String wmsBaseUrl = new URL(restrictionOnLandownershipWmsUrl).getProtocol() + "://" + new URL(restrictionOnLandownershipWmsUrl).getHost() + new URL(restrictionOnLandownershipWmsUrl).getPath();
                    ImageLayer foo = new ImageLayer(restrictionOnLandownership.getTheme().getCode(), new ImageWms(wmsBaseUrl, ImmutableMap.of("LAYERS", restrictionOnLandownershipWmsLayers, "VERSION", "1.1.1")));
                    layerList.add(foo);

                } 
            }
        }
        
        String planForLandRegisterUrl = java.net.URLDecoder.decode(new String(extractType.getRealEstate().getPlanForLandRegister().getReferenceWMS()).trim(), "UTF-8");
        String planForLandRegisterBbox = null;
        String planForLandRegisterLayers = null;
        List<NameValuePair> planForLandRegisterUrlParams = URLEncodedUtils.parse(new URI(planForLandRegisterUrl), Charset.forName("UTF-8"));
        for (NameValuePair param : planForLandRegisterUrlParams) {
            if (param.getName().equalsIgnoreCase("BBOX")) {
                planForLandRegisterBbox = param.getValue();
            }
            if (param.getName().equalsIgnoreCase("LAYERS")) {
                planForLandRegisterLayers = param.getValue();
            } 
        }

        Extent planForLandRegisterExtent = transformBboxToPseudoMercator(planForLandRegisterBbox);
        Coordinate mapCenter = new Coordinate(
                    planForLandRegisterExtent.getMinimum().getX().floatValue() + (planForLandRegisterExtent.getMaximum().getX().floatValue() - planForLandRegisterExtent.getMinimum().getX().floatValue()) / 2, 
                    planForLandRegisterExtent.getMinimum().getY().floatValue() + (planForLandRegisterExtent.getMaximum().getY().floatValue() - planForLandRegisterExtent.getMinimum().getY().floatValue()) / 2);
        
        // Fehlt proj4js oder ähnliches, damit das funktinieren kann? Es wirda aber nie direkt hier transformiert, sondern mit Javascript im Browser.
//        System.out.println(new LongLat(2600000, 1200000, "EPSG:2056").transform("EPGS:3857").getLongitude());
        
        layerList.add(new Tile("USA", new TileWms("https://ahocevar.com/geoserver/wms", ImmutableMap.of("LAYERS", "topp:states", "VERSION", "1.1.1"))));
        layerList.add(new ImageLayer("Grundbuchplan", new ImageWms("https://geowms.bl.ch/", ImmutableMap.of("LAYERS", planForLandRegisterLayers, "VERSION", "1.1.1"))));
                
        add(new DefaultOpenLayersMap("map",
                Model.of(new Map(
                        layerList,
                        new View(mapCenter, 19)))));
    }        
    
    private Extent transformBboxToPseudoMercator(String bbox) {
        String[] bboxArray = bbox.split(",");
        double xMinLV95 = Double.valueOf(bboxArray[0]);
        double yMinLV95 = Double.valueOf(bboxArray[1]);
        double xMaxLV95 = Double.valueOf(bboxArray[2]);
        double yMaxLV95 = Double.valueOf(bboxArray[3]);
        
        double[] minXYWgs84 = ApproxSwissProj.LV95toWGS84(xMinLV95, yMinLV95, 500);
        double[] maxXYWgs84 = ApproxSwissProj.LV95toWGS84(xMaxLV95, yMaxLV95, 500);
                
        double yMinMercator = SphericalMercator.lat2y(minXYWgs84[0]);
        double xMinMercator = SphericalMercator.lon2x(minXYWgs84[1]);
        
        double yMaxMercator = SphericalMercator.lat2y(maxXYWgs84[0]);
        double xMaxMercator = SphericalMercator.lon2x(maxXYWgs84[1]);
        
        Extent extent = new Extent();
        extent.setMaximum(new LongLat(xMaxMercator, yMaxMercator, "EPSG:3857"));
        extent.setMinimum(new LongLat(xMinMercator, yMinMercator, "EPSG:3857"));
        
        return extent;
    }
    
}
