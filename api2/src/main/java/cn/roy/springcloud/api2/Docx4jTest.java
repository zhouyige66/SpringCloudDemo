package cn.roy.springcloud.api2;

import org.docx4j.XmlUtils;
import org.docx4j.dml.CTGraphicalObjectFrameLocking;
import org.docx4j.dml.CTNonVisualDrawingProps;
import org.docx4j.dml.CTNonVisualGraphicFrameProperties;
import org.docx4j.dml.CTPoint2D;
import org.docx4j.dml.wordprocessingDrawing.*;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.PageDimensions;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.org.apache.poi.util.IOUtils;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.docx4j.wml.ObjectFactory;

import javax.xml.bind.JAXBElement;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-10-02 14:51
 * @Version: v1.0
 */
public class Docx4jTest {

    public static InputStream mergeDocx(final List<InputStream> streams) throws Docx4JException, IOException {
        WordprocessingMLPackage target = null;
        //创建临时Docx文件
        final File generated = File.createTempFile("generated", ".docx");

        int chunkId = 0;
        Iterator<InputStream> it = streams.iterator();
        while (it.hasNext()) {
            InputStream is = it.next();
            if (is != null) {
                if (target == null) {
                    // 流读写 第一个文档
                    OutputStream os = new FileOutputStream(generated);
                    os.write(IOUtils.toByteArray(is));
                    os.close();

                    //获取第一个文档
                    target = WordprocessingMLPackage.load(generated);
                } else {
                    // 插入其他文档
                    insertDocx(target.getMainDocumentPart(), IOUtils.toByteArray(is), chunkId++);
                }
            }
        }

        if (target != null) {
            target.save(generated);
            return new FileInputStream(generated);
        } else {
            return null;
        }
    }

    /**
     * * 插入文档
     * * @param main
     * * @param bytes
     * * @param chunkId
     */
    public static void insertDocx(MainDocumentPart main, byte[] bytes, int chunkId) {
        try {
            AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(
                    new PartName("/part" + chunkId + ".docx"));
            // afiPart.setContentType(new ContentType(CONTENT_TYPE));
            afiPart.setBinaryData(bytes);
            Relationship altChunkRel = main.addTargetPart(afiPart);

            CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
            chunk.setId(altChunkRel.getId());

            main.addObject(chunk);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String temporaryWordPath = "C:\\Users\\Roy Z Zhou\\Desktop\\fsdownload\\DLZ-EC-1907-000148-BC-00001.docx";
        String temporaryWordPath2 = "C:\\Users\\Roy Z Zhou\\Desktop\\fsdownload\\EC_template.docx";

        List<String> fileList = new ArrayList<>();
        fileList.add(temporaryWordPath);
        fileList.add(temporaryWordPath2);

        WordprocessingMLPackage target = null;
        try {
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(new File(temporaryWordPath));
            MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
            for (int i = 0; i < fileList.size() - 1; i++) {
                Br br = new Br();
                br.setType(STBrType.PAGE);
                mainDocumentPart.addObject(br);

                AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(
                        new PartName("/part" + i + ".docx"));
                // afiPart.setContentType(new ContentType(CONTENT_TYPE));
                afiPart.setBinaryData(new FileInputStream(new File(fileList.get(i))));
                Relationship altChunkRel = mainDocumentPart.addTargetPart(afiPart);
                CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
                chunk.setId(altChunkRel.getId());

                mainDocumentPart.addObject(chunk);
            }
            wordprocessingMLPackage.save(new FileOutputStream("C:\\Users\\Roy Z Zhou\\Desktop\\fsdownload\\合并.docx"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }

//        String temporaryWordPath2 = "C:\\Users\\Roy Z Zhou\\Desktop\\fsdownload\\DLZ-EC-1907-000148-BC-00001-test.docx";
//        File imgFile = new File("C:\\Users\\Roy Z Zhou\\Pictures\\1.jpeg");
//        try {
//            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(temporaryWordPath));
//            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imgFile);
//            int docPrId = 1;
//            int cNvPrId = 2;
//            Anchor anchor = createImageAnchor(imagePart, "Filename hint", "Alternative text",
//                    docPrId, cNvPrId, false);
//            anchor.setAllowOverlap(true);
//            anchor.setBehindDoc(false);
//            anchor.setDistB(0L);
//            anchor.setDistL(114300L);
//            anchor.setDistR(114300L);
//            anchor.setLayoutInCell(true);
//            anchor.setLocked(false);
//            anchor.setRelativeHeight(251661312);
////            anchor.setSimplePos2(false);
//
//            CTPoint2D ctPoint2D = new CTPoint2D();
//            ctPoint2D.setX(0);
//            ctPoint2D.setY(0);
//            anchor.setSimplePos(ctPoint2D);
//
//            CTPosH ctPosH = new CTPosH();
//            ctPosH.setRelativeFrom(STRelFromH.MARGIN);
//            ctPosH.setAlign(STAlignH.RIGHT);
//            anchor.setPositionH(ctPosH);
//
//            CTPosV ctPosV = new CTPosV();
//            ctPosV.setRelativeFrom(STRelFromV.PARAGRAPH);
//            ctPosV.setPosOffset(-1009015);
//            anchor.setPositionV(ctPosV);
//
////            CTPositiveSize2D ctPositiveSize2D = new CTPositiveSize2D();
////            ctPositiveSize2D.setCx(890270);
////            ctPositiveSize2D.setCy(890270);
////            anchor.setExtent(ctPositiveSize2D);
//
//            CTEffectExtent ctEffectExtent = new CTEffectExtent();
//            ctEffectExtent.setB(5080);
//            ctEffectExtent.setL(0);
//            ctEffectExtent.setR(5080);
//            ctEffectExtent.setT(0);
//            anchor.setEffectExtent(ctEffectExtent);
//
//            anchor.setWrapNone(new CTWrapNone());
//
//            CTNonVisualDrawingProps ctNonVisualDrawingProps = new CTNonVisualDrawingProps();
//            ctNonVisualDrawingProps.setDescr("timg");
//            ctNonVisualDrawingProps.setId(14);
//            ctNonVisualDrawingProps.setName("Picture 14");
//            anchor.setDocPr(ctNonVisualDrawingProps);
//
//            CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties = new CTNonVisualGraphicFrameProperties();
//            CTGraphicalObjectFrameLocking ctGraphicalObjectFrameLocking = new CTGraphicalObjectFrameLocking();
//            ctGraphicalObjectFrameLocking.setNoChangeAspect(true);
//            ctNonVisualGraphicFrameProperties.setGraphicFrameLocks(ctGraphicalObjectFrameLocking);
//            anchor.setCNvGraphicFramePr(ctNonVisualGraphicFrameProperties);
//
//            ObjectFactory objectFactory = new ObjectFactory();
//            Drawing drawing = objectFactory.createDrawing();
//            drawing.getAnchorOrInline().add(anchor);
//            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
//            List<Object> content = documentPart.getContent();
//            P p = (P) content.get(0);
//            p.getContent().add(drawing);
//            wordMLPackage.save(new FileOutputStream(temporaryWordPath2));
//        } catch (org.docx4j.openpackaging.exceptions.InvalidFormatException e) {
//            e.printStackTrace();
//        } catch (Docx4JException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public static Anchor createImageAnchor(BinaryPartAbstractImage image, String filenameHint, String altText, int id1, int id2,
                                           boolean link) throws Exception {
        WordprocessingMLPackage wmlPackage = (WordprocessingMLPackage) image.getPackage();
        List<SectionWrapper> sections = wmlPackage.getDocumentModel().getSections();
        PageDimensions page = ((SectionWrapper) sections.get(sections.size() - 1)).getPageDimensions();
        BinaryPartAbstractImage.CxCy cxcy = BinaryPartAbstractImage.CxCy.scale(image.getImageInfo(), page);
        return createImageAnchor(image, filenameHint, altText, id1, id2, cxcy.getCx(), cxcy.getCy(), link);
    }

    public static Anchor createImageAnchor(BinaryPartAbstractImage image, String filenameHint, String altText, int id1, int id2,
                                           long cx, long cy, boolean link) throws Exception {
        if (filenameHint == null) {
            filenameHint = "";
        }

        if (altText == null) {
            altText = "";
        }

        String type;
        if (link) {
            type = "r:link";
        } else {
            type = "r:embed";
        }

        cx = cy = 890270L;
        String ml =
                "<wp:anchor allowOverlap=\"1\" behindDoc=\"0\" distB=\"0\" distL=\"114300\" distR=\"114300\" distT=\"0\" layoutInCell=\"1\" locked=\"0\" relativeHeight=\"251661312\" simplePos=\"0\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\">\n" +
                        "    <wp:simplePos x=\"0\" y=\"0\"/>\n" +
                        "    <wp:positionH relativeFrom=\"margin\">\n" +
                        "        <wp:align>right</wp:align>\n" +
                        "    </wp:positionH>\n" +
                        "    <wp:positionV relativeFrom=\"paragraph\">\n" +
                        "        <wp:posOffset>-1009015</wp:posOffset>\n" +
                        "    </wp:positionV>\n" +
                        "    <wp:extent cx=\"${cx}\" cy=\"${cy}\"/>\n" +
                        "    <wp:effectExtent b=\"5080\" l=\"0\" r=\"5080\" t=\"0\"/>\n" +
                        "    <wp:wrapNone/>\n" +
                        "    <wp:docPr descr=\"timg\" id=\"14\" name=\"Picture 14\"/>\n" +
                        "    <wp:cNvGraphicFramePr>\n" +
                        "        <a:graphicFrameLocks noChangeAspect=\"1\" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"/>\n" +
                        "    </wp:cNvGraphicFramePr>\n" +
                        "    <a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">\n" +
                        "        <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                        "            <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                        "                <pic:nvPicPr>\n" +
                        "                    <pic:cNvPr id=\"${id2}\" name=\"${filenameHint}\"/>\n" +
                        "                    <pic:cNvPicPr/>\n" +
                        "                </pic:nvPicPr>\n" +
                        "                <pic:blipFill>\n" +
                        "                    <a:blip r:embed=\"${rEmbedId}\"/>\n" +
                        "                    <a:stretch>\n" +
                        "                        <a:fillRect/>\n" +
                        "                    </a:stretch>\n" +
                        "                </pic:blipFill>\n" +
                        "                <pic:spPr>\n" +
                        "                    <a:xfrm>\n" +
                        "                        <a:off x=\"0\" y=\"0\"/>\n" +
                        "                        <a:ext cx=\"${cx}\" cy=\"${cy}\"/>\n" +
                        "                    </a:xfrm>\n" +
                        "                    <a:prstGeom prst=\"rect\">\n" +
                        "                        <a:avLst/>\n" +
                        "                    </a:prstGeom>\n" +
                        "                </pic:spPr>\n" +
                        "            </pic:pic>\n" +
                        "        </a:graphicData>\n" +
                        "    </a:graphic>\n" +
                        "</wp:anchor>";
        HashMap<String, String> mappings = new HashMap();
        mappings.put("cx", Long.toString(cx));
        mappings.put("cy", Long.toString(cy));
        mappings.put("filenameHint", filenameHint);
        mappings.put("altText", altText);
        mappings.put("rEmbedId", getRelLast(image).getId());
        mappings.put("id1", Integer.toString(id1));
        mappings.put("id2", Integer.toString(id2));
        Object o = XmlUtils.unmarshallFromTemplate(ml, mappings);
        Anchor anchor = (Anchor) ((JAXBElement) o).getValue();

        return anchor;
    }

    public static Relationship getRelLast(BinaryPartAbstractImage image) {
        int size = image.getRels().size();
        return size < 1 ? null : (Relationship) image.getRels().get(size - 1);
    }

}
