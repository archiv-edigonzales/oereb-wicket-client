package ch.so.agi.oereb.wicketclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ThemeType;
import ch.so.agi.oereb.wicketclient.services.ExtractService;


//import ch.ehi.oereb.schemas.gml._3_2.MultiSurface;
//import ch.ehi.oereb.schemas.gml._3_2.MultiSurfacePropertyTypeType;
//import ch.ehi.oereb.schemas.gml._3_2.MultiSurfaceTypeType;
//import ch.ehi.oereb.schemas.oereb._1_0.extract.GetCapabilitiesResponse;
//import ch.ehi.oereb.schemas.oereb._1_0.extract.GetCapabilitiesResponseType;
//import ch.ehi.oereb.schemas.oereb._1_0.extract.GetEGRIDResponse;
//import ch.ehi.oereb.schemas.oereb._1_0.extract.GetEGRIDResponseType;
//import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponse;
//import ch.ehi.oereb.schemas.oereb._1_0.extract.GetExtractByIdResponseType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.CantonCodeType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ExclusionOfLiabilityType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.Extract;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ExtractType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.GlossaryType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.LanguageCodeType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.LocalisedMTextType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.LocalisedTextType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.MapType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.MultilingualMTextType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.MultilingualTextType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.OfficeType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RealEstateDPRType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.RealEstateTypeType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.ThemeType;
//import ch.ehi.oereb.schemas.oereb._1_0.extractdata.WebReferenceType;
//import ch.ehi.oereb.schemas.oereb._1_0.versioning.GetVersionsResponse;
//import ch.ehi.oereb.schemas.oereb._1_0.versioning.GetVersionsResponseType;
//import ch.ehi.oereb.schemas.oereb._1_0.versioning.VersionType;

@Controller
public class MainController {
    
    protected static final String extractNS = "http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract";
    
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    private String webServiceUrl = "https://geoview.bl.ch/main/oereb/extract/reduced/xml/geometry/";
            
    @Autowired
    Jaxb2Marshaller marshaller;

    @Autowired
    ExtractService extractService;
    
    @GetMapping("/")
    public ResponseEntity<String>  ping() {
        return new ResponseEntity<String>("oereb wicket client", HttpStatus.OK);
    }
   
    /*
     * https://example.com/oereb/extract/reduced/xml/CH158782774974
     * http://localhost:8080/extract/reduced/xml/CH158782774974
     */
    @GetMapping(value="/extract/reduced/xml/{egrid}", consumes=MediaType.ALL_VALUE)
    public ResponseEntity<?> getExtractByEgrid(@PathVariable String egrid) throws IOException {
        logger.info(egrid);
        
//        extractService.getExtractByEgrid(egrid);
        
        File xmlFile = Files.createTempFile("data_extract_tmp_", ".xml").toFile();
//        logger.info("Temp file : " + xmlFile.getAbsolutePath());

        // Use extract from local resources. 
//        InputStream xmlResource = new ClassPathResource("CH567107399166_geometry_images.xml").getInputStream();        
//        Files.copy(xmlResource, xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//        IOUtils.closeQuietly(xmlResource);

        // Download extract from web service.
        URL url = new URL(webServiceUrl + egrid);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        try (FileOutputStream xmlOutputStream = new FileOutputStream(xmlFile)) {
            xmlOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        logger.info("File downloaded: " + xmlFile.getAbsolutePath());
        
        // Process data extract.
        StreamSource xmlSource = new StreamSource(xmlFile);
        logger.info(xmlSource.toString());

        GetExtractByIdResponse obj = (GetExtractByIdResponse) marshaller.unmarshal(xmlSource);
        logger.info(obj.getValue().getExtract().getValue().getExtractIdentifier());
        logger.info(obj.getValue().getExtract().getValue().getRealEstate().getMunicipality());
        
        List<ThemeType> concernedThemes = obj.getValue().getExtract().getValue().getConcernedTheme();
        for (ThemeType theme : concernedThemes) {
            logger.info(theme.getCode());
            logger.info(theme.getText().getText());
            logger.info("-----");
        }
        
        return new ResponseEntity<String>("oereb wicket client", HttpStatus.OK);
    }
    
    
    
    
    /* 
     * https://example.com/oereb/getegrid/xml/?XY=608000,228000
     * https://example.com/oereb/getegrid/json/BE0200000332/100
     * https://example.com/oereb/getegrid/json/3084/Lindenweg/50
     * https://example.com/oereb/getegrid/xml/?GNSS=46.94890,7.44665
     * ${baseurl}/getegrid/${FORMAT}/?XY=${XY}/
     * ${baseurl}/getegrid/${FORMAT}/${IDENTDN}/${NUMBER}
     * ${baseurl}/getegrid/${FORMAT}/${POSTALCODE}/${LOCALISATION}/${NUMBER}
     * ${baseurl}/getegrid/${FORMAT}/?GNSS=${GNSS}
     */
//    @GetMapping("/getegrid/{format}/{identdn}/{number}")
//    public ResponseEntity<GetEGRIDResponse>  getEgridByNumber(@PathVariable String format, @PathVariable String identdn,@PathVariable String number) {
//        if(!format.equals("xml")) {
//            throw new IllegalArgumentException("unsupported format <"+format+">");
//        }
//        GetEGRIDResponseType ret= new GetEGRIDResponseType();
//        List<JAXBElement<String>[]> gsList=jdbcTemplate.query(
//                "SELECT egris_egrid,nummer,nbident FROM "+getSchema()+".dm01vch24lv95dliegenschaften_grundstueck WHERE nummer=? AND nbident=?", new RowMapper<JAXBElement<String>[]>() {
//                    @Override
//                    public JAXBElement<String>[] mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        JAXBElement<String> ret[]=new JAXBElement[3];
//                        ret[0]=new JAXBElement<String>(new QName(extractNS,"egrid"),String.class,rs.getString(1));
//                        ret[1]=new JAXBElement<String>(new QName(extractNS,"number"),String.class,rs.getString(2));
//                        ret[2]=new JAXBElement<String>(new QName(extractNS,"identDN"),String.class,rs.getString(3));
//                        return ret;
//                    }
//                    
//                },number,identdn);
//        for(JAXBElement<String>[] gs:gsList) {
//            ret.getEgridAndNumberAndIdentDN().add(gs[0]);
//            ret.getEgridAndNumberAndIdentDN().add(gs[1]);
//            ret.getEgridAndNumberAndIdentDN().add(gs[2]);
//        }
//         return new ResponseEntity<GetEGRIDResponse>(new GetEGRIDResponse(ret),gsList.size()>0?HttpStatus.OK:HttpStatus.NO_CONTENT);
//    }
//    @GetMapping("/getegrid/{format}")
//    public ResponseEntity<GetEGRIDResponse>  getEgridByXY(@PathVariable String format,@RequestParam(value="XY", required=false) String xy,@RequestParam(value="GNSS", required=false) String gnss) {
//        if(!format.equals("xml")) {
//            throw new IllegalArgumentException("unsupported format <"+format+">");
//        }
//        if(xy==null && gnss==null) {
//            throw new IllegalArgumentException("parameter XY or GNSS required");
//        }else if(xy!=null && gnss!=null) {
//            throw new IllegalArgumentException("only one of parameters XY or GNSS is allowed");
//        }
//        Coordinate coord = null;
//        int srid = 2056;
//        double scale = 1000.0;
//        if(xy!=null) {
//            coord=parseCoord(xy);
//            srid = 2056;
//            if(coord.x<2000000.0) {
//                srid=21781;
//            }
//        }else {
//            coord=parseCoord(gnss);
//            srid = 4326;
//            scale=100000.0;
//        }
//        WKBWriter geomEncoder=new WKBWriter(2,ByteOrderValues.BIG_ENDIAN,true);
//        PrecisionModel precisionModel=new PrecisionModel(scale);
//        GeometryFactory geomFact=new GeometryFactory(precisionModel,srid);
//        byte geom[]=geomEncoder.write(geomFact.createPoint(coord));
//        // SELECT g.egris_egrid,g.nummer,g.nbident FROM oereb.dm01vch24lv95dliegenschaften_grundstueck g LEFT JOIN oereb.dm01vch24lv95dliegenschaften_liegenschaft l ON l.liegenschaft_von=g.t_id WHERE ST_DWithin(ST_GeomFromEWKT('SRID=2056;POINT( 2638242.500 1251450.000)'),l.geometrie,1.0)
//        // SELECT g.egris_egrid,g.nummer,g.nbident FROM oereb.dm01vch24lv95dliegenschaften_grundstueck g LEFT JOIN oereb.dm01vch24lv95dliegenschaften_liegenschaft l ON l.liegenschaft_von=g.t_id WHERE ST_DWithin(ST_Transform(ST_GeomFromEWKT('SRID=4326;POINT( 7.94554 47.41277)'),2056),l.geometrie,1.0)
//        GetEGRIDResponseType ret= new GetEGRIDResponseType();
//        List<JAXBElement<String>[]> gsList=jdbcTemplate.query(
//                "SELECT egris_egrid,nummer,nbident FROM "+getSchema()+".dm01vch24lv95dliegenschaften_grundstueck g LEFT JOIN "+getSchema()+".dm01vch24lv95dliegenschaften_liegenschaft l ON l.liegenschaft_von=g.t_id WHERE ST_DWithin(ST_Transform(?,2056),l.geometrie,1.0)", new RowMapper<JAXBElement<String>[]>() {
//                    @Override
//                    public JAXBElement<String>[] mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        JAXBElement<String> ret[]=new JAXBElement[3];
//                        ret[0]=new JAXBElement<String>(new QName(extractNS,"egrid"),String.class,rs.getString(1));
//                        ret[1]=new JAXBElement<String>(new QName(extractNS,"number"),String.class,rs.getString(2));
//                        ret[2]=new JAXBElement<String>(new QName(extractNS,"identDN"),String.class,rs.getString(3));
//                        return ret;
//                    }
//                    
//                },geom);
//        for(JAXBElement<String>[] gs:gsList) {
//            ret.getEgridAndNumberAndIdentDN().add(gs[0]);
//            ret.getEgridAndNumberAndIdentDN().add(gs[1]);
//            ret.getEgridAndNumberAndIdentDN().add(gs[2]);
//        }
//         return new ResponseEntity<GetEGRIDResponse>(new GetEGRIDResponse(ret),gsList.size()>0?HttpStatus.OK:HttpStatus.NO_CONTENT);
//    }
//    
//    /*
//     * 
//     *   https://example.com/oereb/extract/reduced/xml/CH887722167773
//     *   https://example.com/oereb/extract/reduced/xml/geometry/CH887722167773
//     *   https://example.com/oereb/extract/full/pdf/BE0200000332/100
//     *   ${baseurl}/extract/${FLAVOUR}/${FORMAT}[/${GEOMETRY}]/${EGRID}[?LANG=${LANG}&TOPICS=${TOPICS}&WITHIMAGES]
//     *   ${baseurl}/extract/${FLAVOUR}/${FORMAT}[/${GEOMETRY}]/${IDENTDN}/${NUMBER}[?LANG=${LANG}&TOPICS=${TOPICS}&WITHIMAGES]
//     */
//                
//    @GetMapping(value="/extract/reduced/{format}/{geometry}/{egrid}",consumes=MediaType.ALL_VALUE,produces = {MediaType.APPLICATION_PDF_VALUE,MediaType.APPLICATION_XML_VALUE})
//    public ResponseEntity<?>  getExtractWithGeometryByEgrid(@PathVariable String format,@PathVariable String geometry,@PathVariable String egrid,@RequestParam(value="LANG", required=false) String lang,@RequestParam(value="TOPICS", required=false) String topics,@RequestParam(value="WITHIMAGES", required=false) String withImages) {
//        if(!format.equals("xml") && !format.equals("pdf")) {
//            throw new IllegalArgumentException("unsupported format <"+format+">");
//        }
//        Grundstueck parcel=getParcelByEgrid(egrid);
//        if(parcel==null) {
//            return new ResponseEntity<Object>(HttpStatus.NO_CONTENT);
//        }
//        Extract extract=createExtract(egrid,parcel,true,lang,topics,withImages==null?false:true);
//        
//        GetExtractByIdResponseType response=new GetExtractByIdResponseType();
//        response.setExtract(extract);
//        GetExtractByIdResponse responseEle=new GetExtractByIdResponse(response);
//        
//        if(format.equals("pdf")) {
//            java.io.File tmpFolder=new java.io.File(System.getProperty("java.io.tmpdir"),"oerebws"+Thread.currentThread().getId());
//            if(!tmpFolder.exists()) {
//                tmpFolder.mkdirs();
//            }
//            logger.info("tmpFolder {}",tmpFolder.getAbsolutePath());
//            java.io.File tmpExtractFile=new java.io.File(tmpFolder,egrid+".xml");
//            marshaller.marshal(responseEle,new javax.xml.transform.stream.StreamResult(tmpExtractFile));
//            try {
//                java.io.File pdfFile=extractXml2pdf.runXml2Pdf(tmpExtractFile.getAbsolutePath(), tmpFolder.getAbsolutePath(), Locale.DE);
//                String pdfFilename = pdfFile.getName();
//                /*
//                HttpHeaders headers = new HttpHeaders();
//                headers.setContentType(MediaType.APPLICATION_PDF);
//                headers.add("Access-Control-Allow-Origin", "*");
//                //headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
//                headers.add("Access-Control-Allow-Headers", "Content-Type");
//                headers.add("Content-Disposition", "filename=" + pdfFilename);
//                headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
//                headers.add("Pragma", "no-cache");
//                headers.add("Expires", "0");
//                headers.setContentLength(pdfFile.length());
//                return new ResponseEntity<java.io.FileInputStream>(
//                        new java.io.FileInputStream(pdfFile), headers, HttpStatus.OK);                
//                */
//                java.io.InputStream is = new java.io.FileInputStream(pdfFile);
//                return ResponseEntity
//                        .ok().header("content-disposition", "attachment; filename=" + pdfFile.getName())
//                        .contentLength(pdfFile.length())
//                        .contentType(MediaType.APPLICATION_PDF).body(new InputStreamResource(is));                
//            } catch (ConverterException e) {
//                throw new IllegalStateException(e);
//            } catch (FileNotFoundException e) {
//                throw new IllegalStateException(e);
//            }
//        }
//        return new ResponseEntity<GetExtractByIdResponse>(responseEle,HttpStatus.OK);
//    }    
//
//    @GetMapping("/extract/reduced/{format}/{egrid}")
//    public ResponseEntity<?>  getExtractWithoutGeometryByEgrid(@PathVariable String format,@PathVariable String geometry,@PathVariable String egrid,@RequestParam(value="LANG", required=false) String lang,@RequestParam(value="TOPICS", required=false) String topics,@RequestParam(value="WITHIMAGES", required=false) String withImages) {
//        if(!format.equals("xml")) {
//            throw new IllegalArgumentException("unsupported format <"+format+">");
//        }
//        return null;
//    }    
//    @GetMapping("/extract/reduced/{format}/{geometry}/{identdn}/{number}")
//    public ResponseEntity<?>  getExtractWithGeometryByNumber(@PathVariable String format,@PathVariable String geometry,@PathVariable String identdn,@PathVariable String number,@RequestParam(value="LANG", required=false) String lang,@RequestParam(value="TOPICS", required=false) String topics,@RequestParam(value="WITHIMAGES", required=false) String withImages) {
//        if(!format.equals("xml")) {
//            throw new IllegalArgumentException("unsupported format <"+format+">");
//        }
//        return null;
//    }    
//    @GetMapping("/capabilities/{format}")
//    public @ResponseBody  GetCapabilitiesResponse getCapabilities(@PathVariable String format) {
//        if(!format.equals("xml")) {
//            throw new IllegalArgumentException("unsupported format <"+format+">");
//        }
//        GetCapabilitiesResponseType ret=new GetCapabilitiesResponseType();
//        
//        // Liste der vorhandenen OeREB-Katasterthemen (inkl. Kantons- und Gemeindethemen);
//        setThemes(ret.getTopic(),getTopics());
//        
//        // Liste der vorhandenen Gemeinden;
//        List<Integer> gemeinden=jdbcTemplate.query(
//                "SELECT bfsnr FROM "+getSchema()+".dm01vch24lv95dgemeindegrenzen_gemeinde", new RowMapper<Integer>() {
//                    @Override
//                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        return rs.getInt(1);
//                    }
//                    
//                });
//        ret.getMunicipality().addAll(gemeinden);
//        // Liste der vorhandenen FLAVOURs;
//        ret.getFlavour().add("reduced");
//        // Liste der unterstuetzten Sprachen (2 stellige ISO Codes);
//        ret.getLanguage().add("de");
//        // Liste der unterstuetzten CRS.
//        ret.getCrs().add("2056");
//        return new GetCapabilitiesResponse(ret);
//    }
//    @GetMapping("/versions/{format}")
//    public @ResponseBody  GetVersionsResponse getVersions(@PathVariable String format) {
//        if(!format.equals("xml")) {
//            throw new IllegalArgumentException("unsupported format <"+format+">");
//        }
//        GetVersionsResponseType ret=new GetVersionsResponseType();
//        VersionType ver=new VersionType();
//        ver.setVersion("extract-1.0");
//        ret.getSupportedVersion().add(ver);
//        return new GetVersionsResponse(ret);
//    }
//    
//    private Extract createExtract(String egrid, Grundstueck parcel, boolean withGeometry, String lang, String requestedTopicsAsText, boolean withImages) {
//        ExtractType extract=new ExtractType();
//        extract.setIsReduced(true);
//        XMLGregorianCalendar today=null;
//        try {
//            GregorianCalendar gdate=new GregorianCalendar();
//            gdate.setTime(new java.util.Date());
//            today = DatatypeFactory.newInstance().newXMLGregorianCalendar(gdate);
//        } catch (DatatypeConfigurationException e) {
//            throw new IllegalStateException(e);
//        }
//        extract.setCreationDate(today);
//        extract.setExtractIdentifier(UUID.randomUUID().toString());
//        List<String> requestedTopics=parseTopics(requestedTopicsAsText);
//        // Grundstueck
//        setParcel(extract,egrid,parcel,withGeometry);
//        int bfsNr=extract.getRealEstate().getFosNr();
//        // freigeschaltete Themen in der betroffenen Gemeinde
//        List<String> availableTopics=getTopics(bfsNr);
//        List<String> queryTopics=new ArrayList<String>();
//        queryTopics.addAll(availableTopics);
//        queryTopics.retainAll(requestedTopics);
//        List<String> concernedTopics=new ArrayList<String>();
//        addRestrictions(extract,parcel.getGeometrie(),withGeometry,withImages,queryTopics,concernedTopics);
//        // Themen
//        List<String> themeWithoutData=new ArrayList<String>();
//        themeWithoutData.addAll(requestedTopics);
//        themeWithoutData.removeAll(availableTopics);
//        List<String> notConcernedTopics=new ArrayList<String>();
//        notConcernedTopics.addAll(queryTopics);
//        notConcernedTopics.removeAll(concernedTopics);
//        setThemes(extract.getConcernedTheme(), concernedTopics);
//        setThemes(extract.getNotConcernedTheme(), notConcernedTopics);
//        setThemes(extract.getThemeWithoutData(), themeWithoutData);
//        // Logos
//        extract.setLogoPLRCadastre(getImage("ch.plr"));
//        extract.setFederalLogo(getImage("ch"));
//        extract.setCantonalLogo(getImage("ch."+extract.getRealEstate().getCanton().name().toLowerCase()));
//        extract.setMunicipalityLogo(getImage("ch."+extract.getRealEstate().getFosNr()));
//        // Text
//        setBaseData(extract);
//        setGeneralInformation(extract);
//        setExclusionOfLiability(extract);
//        setGlossary(extract);
//        // Oereb-Amt
//        OfficeType plrCadastreAuthority = new OfficeType();
//        WebReferenceType webRef=new WebReferenceType();
//        webRef.setValue(plrCadastreAuthorityUrl);
//        plrCadastreAuthority.setOfficeAtWeb(webRef);
//
//        setOffice(plrCadastreAuthority);
//        extract.setPLRCadastreAuthority(plrCadastreAuthority);
//        
//        return new Extract(extract);
//    }
//
//    private byte[] getImage(String code) {
//        java.util.List<byte[]> baseData=jdbcTemplate.queryForList(
//                "SELECT logo FROM "+getSchema()+".oerb_xtnx_v1_0annex_logo WHERE acode=?",byte[].class,code);
//        if(baseData!=null && baseData.size()==1) {
//            return baseData.get(0);
//        }
//        return minimalImage;
//    }
//
//    private void setOffice(OfficeType office) {
//        java.util.Map<String,Object> baseData=jdbcTemplate.queryForMap(
//                "SELECT aname_de,auid,line1,line2,street,anumber,postalcode,city FROM "+getSchema()+".oerb_xtnx_v1_0annex_office WHERE officeatweb=?",office.getOfficeAtWeb().getValue());
//        if(baseData!=null) {
//            office.setName(createMultilingualTextType(baseData, "aname"));
//            office.setUID((String) baseData.get("auid"));
//            office.setLine1((String) baseData.get("line2"));
//            office.setLine2((String) baseData.get("line1"));
//            office.setStreet((String) baseData.get("street"));
//            office.setNumber((String) baseData.get("anumber"));
//            office.setPostalCode((String) baseData.get("postalcode"));
//            office.setCity((String) baseData.get("city"));
//        }
//    }
//
//    private void setGlossary(ExtractType extract) {
//        java.util.List<java.util.Map<String,Object>> baseDataList=jdbcTemplate.queryForList(
//                "SELECT title_de,title_fr,title_it,title_rm,title_en,content_de,content_fr,content_it,content_rm,content_en FROM "+getSchema()+".oerb_xtnx_v1_0annex_glossary");
//        for(java.util.Map<String,Object> baseData:baseDataList) {
//            MultilingualMTextType content = createMultilingualMTextType(baseData,"content");
//            MultilingualTextType title = createMultilingualTextType(baseData,"title");
//            GlossaryType glossary=new GlossaryType();
//            glossary.setContent(content);
//            glossary.setTitle(title);
//            extract.getGlossary().add(glossary);
//        }
//    }
//
//    private void setExclusionOfLiability(ExtractType extract) {
//        java.util.List<java.util.Map<String,Object>> baseDataList=jdbcTemplate.queryForList(
//                "SELECT title_de,title_fr,title_it,title_rm,title_en,content_de,content_fr,content_it,content_rm,content_en FROM "+getSchema()+".oerb_xtnx_v1_0annex_exclusionofliability");
//        for(java.util.Map<String,Object> baseData:baseDataList) {
//            MultilingualMTextType content = createMultilingualMTextType(baseData,"content");
//            MultilingualTextType title = createMultilingualTextType(baseData,"title");
//            ExclusionOfLiabilityType exclOfLiab=new ExclusionOfLiabilityType();
//            exclOfLiab.setContent(content);
//            exclOfLiab.setTitle(title);
//            extract.getExclusionOfLiability().add(exclOfLiab);
//        }
//    }
//
//    private void setGeneralInformation(ExtractType extract) {
//        java.util.Map<String,Object> baseData=jdbcTemplate.queryForMap(
//                "SELECT content_de,content_fr,content_it,content_rm,content_en FROM "+getSchema()+".oerb_xtnx_v1_0annex_generalinformation");
//        extract.setGeneralInformation(createMultilingualMTextType(baseData,"content"));
//    }
//
//    private void setBaseData(ExtractType extract) {
//        java.util.Map<String,Object> baseData=jdbcTemplate.queryForMap(
//                "SELECT content_de,content_fr,content_it,content_rm,content_en FROM "+getSchema()+".oerb_xtnx_v1_0annex_basedata");
//        extract.setBaseData(createMultilingualMTextType(baseData,"content"));
//    }
//
//    private MultilingualMTextType createMultilingualMTextType(Map<String, Object> baseData,String prefix) {
//        MultilingualMTextType ret=new MultilingualMTextType();
//        for(LanguageCodeType lang:LanguageCodeType.values()) {
//            String txt=(String)baseData.get(prefix+"_"+lang.value());
//            if(txt!=null) {
//                LocalisedMTextType lTxt= new LocalisedMTextType();
//                lTxt.setLanguage(lang);
//                lTxt.setText(txt);
//                ret.getLocalisedText().add(lTxt);
//            }
//        }
//        return ret;
//    }
//    private MultilingualTextType createMultilingualTextType(Map<String, Object> baseData,String prefix) {
//        MultilingualTextType ret=new MultilingualTextType();
//        for(LanguageCodeType lang:LanguageCodeType.values()) {
//            String txt=(String)baseData.get(prefix+"_"+lang.value());
//            if(txt!=null) {
//                LocalisedTextType lTxt= new LocalisedTextType();
//                lTxt.setLanguage(lang);
//                lTxt.setText(txt);
//                ret.getLocalisedText().add(lTxt);
//            }
//        }
//        return ret;
//    }
//
//    private String getSchema() {
//        return dbschema!=null?dbschema:"xoereb";
//    }
//    
//    private Coordinate parseCoord(String xy) {
//        int sepPos=xy.indexOf(',');
//        double x=Double.parseDouble(xy.substring(0, sepPos));
//        double y=Double.parseDouble(xy.substring(sepPos+1));
//        Coordinate coord=new Coordinate(x,y);
//        return coord;
//    }
//    private Grundstueck getParcelByEgrid(String egrid) {
//        PrecisionModel precisionModel=new PrecisionModel(1000.0);
//        GeometryFactory geomFactory=new GeometryFactory(precisionModel);
//        List<Grundstueck> gslist=jdbcTemplate.query(
//                "SELECT ST_AsBinary(geometrie),nummer,nbident,art,gesamteflaechenmass,flaechenmass FROM "+getSchema()+".dm01vch24lv95dliegenschaften_grundstueck g LEFT JOIN "+getSchema()+".dm01vch24lv95dliegenschaften_liegenschaft l ON g.t_id=l.liegenschaft_von WHERE g.egris_egrid=?", new RowMapper<Grundstueck>() {
//                    WKBReader decoder=new WKBReader(geomFactory);
//                    
//                    @Override
//                    public Grundstueck mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        Geometry polygon=null;
//                        try {
//                            polygon=decoder.read(rs.getBytes(1));
//                            if(polygon==null || polygon.isEmpty()) {
//                                return null;
//                            }
//                        } catch (ParseException e) {
//                            throw new IllegalStateException(e);
//                        }
//                        Grundstueck ret=new Grundstueck();
//                        ret.setGeometrie(polygon);
//                        ret.setEgrid(egrid);
//                        ret.setNummer(rs.getString(2));
//                        ret.setNbident(rs.getString(3));
//                        ret.setArt(rs.getString(4));
//                        int f=rs.getInt(5);
//                        if(rs.wasNull()) {
//                            f=rs.getInt(6);
//                        }
//                        ret.setFlaechenmas(f);
//                        return ret;
//                    }
//
//                    
//                },egrid);
//        if(gslist==null || gslist.isEmpty()) {
//            return null;
//        }
//        Polygon polygons[]=new Polygon[gslist.size()];
//        int i=0;
//        for(Grundstueck gs:gslist) {
//            polygons[i++]=(Polygon)gs.getGeometrie();
//        }
//        Geometry multiPolygon=geomFactory.createMultiPolygon(polygons);
//        Grundstueck gs=gslist.get(0);
//        gs.setGeometrie(multiPolygon);
//        return gs;
//    }
//    private Geometry getParcelGeometryByEgrid(String egrid) {
//        byte[] geom=jdbcTemplate.queryForObject(
//                "SELECT ST_AsBinary(ST_Collect(geometrie)) FROM "+getSchema()+".dm01vch24lv95dliegenschaften_grundstueck g LEFT JOIN "+getSchema()+".dm01vch24lv95dliegenschaften_liegenschaft l ON g.egris_egrid=?", new RowMapper<byte[]>() {
//                    @Override
//                    public byte[] mapRow(ResultSet rs, int rowNum) throws SQLException {
//                        return rs.getBytes(1);
//                    }
//                    
//                },egrid);
//        if(geom==null) {
//            return null;
//        }
//        PrecisionModel precisionModel=new PrecisionModel(1000.0);
//        GeometryFactory geomFactory=new GeometryFactory(precisionModel);
//        WKBReader decoder=new WKBReader(geomFactory);
//        Geometry polygon=null;
//        try {
//            polygon=decoder.read(geom);
//            if(polygon==null || polygon.isEmpty()) {
//                return null;
//            }
//        } catch (ParseException e) {
//            throw new IllegalStateException(e);
//        }
//        
//        return polygon;
//    }
//    public void setThemes(final List<ThemeType> themes, List<String> topicCodes) {
//        for(String theme:topicCodes) {
//            ThemeType themeEle=new ThemeType();
//            themeEle.setCode(theme);
//            themeEle.setText(getTopicText(theme));
//            themes.add(themeEle);
//        }
//    }
//
//    private void addRestrictions(ExtractType extract, Geometry parcelGeom, boolean withGeometry, boolean withImages,
//            List<String> queryTopics, List<String> concernedTopics) {
//        // TODO Auto-generated method stub
//        
//    }
//
//    private void setParcel(ExtractType extract, String egrid, Grundstueck parcel, boolean withGeometry) {
//        WKBWriter geomEncoder=new WKBWriter(2,ByteOrderValues.BIG_ENDIAN);
//        byte geom[]=geomEncoder.write(parcel.getGeometrie());
//        
//        RealEstateDPRType gs = new  RealEstateDPRType();
//        gs.setEGRID(egrid);
//        final String nbident = parcel.getNbident();
//        String canton=nbident.substring(0, 2);
//        gs.setCanton(CantonCodeType.fromValue(canton));
//        gs.setIdentDN(nbident);
//        gs.setNumber(parcel.getNummer());
//        if(false) {
//            List<Object[]> gslist=jdbcTemplate.query(
//                    "SELECT aname,bfsnr FROM "+getSchema()+".dm01vch24lv95dgemeindegrenzen_gemeinde g LEFT JOIN "+getSchema()+".dm01vch24lv95dgemeindegrenzen_gemeindegrenze l ON g.t_id=l.gemeindegrenze_von WHERE ST_Intersects(l.geometrie,ST_GeomFromWKB(?,2056))", new RowMapper<Object[]>() {
//                        
//                        @Override
//                        public Object[] mapRow(ResultSet rs, int rowNum) throws SQLException {
//                            Object ret[]=new Object[2];
//                            ret[0]=rs.getString(1);
//                            ret[1]=rs.getInt(2);
//                            return ret;
//                        }
//
//                        
//                    },geom);
//            if(gslist==null || gslist.isEmpty()) {
//                return;
//            }
//            String gemeindename=(String) gslist.get(0)[0];
//            int bfsnr=(Integer) gslist.get(0)[1];
//            gs.setFosNr(bfsnr);
//            gs.setMunicipality(gemeindename);
//            
//        }else {
//            // grundbuchkreis
//            java.util.Map<String,Object> gbKreis=jdbcTemplate.queryForMap(
//                    "SELECT aname,bfsnr FROM "+getSchema()+".so_g_v_0180822grundbuchkreise_grundbuchkreis WHERE nbident=?",nbident);
//            gs.setSubunitOfLandRegister((String)gbKreis.get("aname"));
//            gs.setFosNr((Integer)gbKreis.get("bfsnr"));
//            // gemeindename
//            String gemeindename=jdbcTemplate.queryForObject(
//                    "SELECT aname FROM "+getSchema()+".dm01vch24lv95dgemeindegrenzen_gemeinde WHERE bfsnr=?",String.class,gs.getFosNr());
//            gs.setMunicipality(gemeindename);
//        }
//        gs.setLandRegistryArea((int)parcel.getFlaechenmas());
//        gs.setType(RealEstateTypeType.REAL_ESTATE);
//        //gs.setMetadataOfGeographicalBaseData(value);
//        // geometry must be set here (because xml2pdf requires it), even if is not request by service client
//        MultiSurfacePropertyTypeType geomGml=new Jts2GML32().convertMultiSurface(parcel.getGeometrie());
//        gs.setLimit(geomGml);
//        {
//            MapType planForLandregister=new MapType();
//            planForLandregister.setImage(minimalImage);
//            gs.setPlanForLandRegister(planForLandregister);
//        }
//        {
//            MapType planForLandregisterMainPage=new MapType();
//            planForLandregisterMainPage.setImage(minimalImage);
//            gs.setPlanForLandRegisterMainPage(planForLandregisterMainPage);
//        }
//        extract.setRealEstate(gs);
//        
//    }
//
//
//    private List<String> parseTopics(String requestedTopicsAsText) {
//        if(requestedTopicsAsText==null || requestedTopicsAsText.length()==0) {
//            requestedTopicsAsText="ALL";
//        }
//        java.util.Set<String> ret=new java.util.HashSet<String>();
//        String topicsx[]=requestedTopicsAsText.split(";");
//        for(String topic:topicsx) {
//            if(topic.equals("ALL_FEDERAL") || topic.equals("ALL")) {
//                ret.add("Nutzungsplanung");
//                ret.add("ProjektierungszonenNationalstrassen");
//                ret.add("BaulinienNationalstrassen");
//                ret.add("ProjektierungszonenEisenbahnanlagen");
//                ret.add("BaulinienEisenbahnanlagen");
//                ret.add("ProjektierungszonenFlughafenanlagen");
//                ret.add("BaulinienFlughafenanlagen");
//                ret.add("SicherheitszonenplanFlughafen");
//                ret.add("BelasteteStandorte");
//                ret.add("BelasteteStandorteMilitaer");
//                ret.add("BelasteteStandorteZivileFlugplaetze");
//                ret.add("BelasteteStandorteOeffentlicherVerkehr");
//                ret.add("Grundwasserschutzzonen");
//                ret.add("Grundwasserschutzareale");
//                ret.add("Laermemfindlichkeitsstufen");
//                ret.add("Waldgrenzen");
//                ret.add("Waldabstandslinien");
//                if(topic.equals("ALL")) {
//                    java.util.List<String> baseDataList=jdbcTemplate.queryForList(
//                            "SELECT othercode FROM "+getSchema()+".oerebkrm_v1_1codelistentext_thematxt WHERE acode='WeiteresThema'",String.class);
//                    for(String extTopic:baseDataList) {
//                        ret.add(extTopic);
//                    }
//                }
//            }else {
//                ret.add(topic);
//            }
//            
//        }
//        return new ArrayList<String>(ret);
//    }
//
//    private LocalisedTextType getTopicText(String theme) {
//        String title_de=jdbcTemplate.queryForObject(
//                "SELECT titel_de FROM "+getSchema()+".oerebkrm_v1_1codelistentext_thematxt WHERE acode=? OR othercode=?",String.class,theme,theme);
//        LocalisedTextType ret=new LocalisedTextType();
//        ret.setLanguage(LanguageCodeType.DE);
//        ret.setText(title_de);
//        return ret;
//    }
//
//    private List<String> getTopics(int bfsNr) {
//        List<String> ret=jdbcTemplate.queryForList("SELECT avalue from "+getSchema()+".oereb_extractannex_v1_0_code_ as c JOIN "+getSchema()+".oerb_xtnx_v1_0annex_municipalitywithplrc as m On c.oerb_xtnx_vpltywthplrc_themes=m.t_id WHERE m.municipality=?",String.class,bfsNr);
//        return ret;
//    }
//    private List<String> getTopics() {
//        List<String> ret=jdbcTemplate.queryForList("SELECT DISTINCT avalue from "+getSchema()+".oereb_extractannex_v1_0_code_",String.class);
//        return ret;
//    }
}