package cn.roy.springcloud.api2;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.docx4j.XmlUtils;
import org.docx4j.dml.*;
import org.docx4j.dml.picture.CTPictureNonVisual;
import org.docx4j.dml.picture.Pic;
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
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Description: 测试
 * @Author: Roy Z
 * @Date: 2019-10-02 14:51
 * @Version: v1.0
 */
public class Docx4jTest {

    public static void main(String[] args) {
        mergeTest();
    }

    public static void addQRCodeByCodeTest() {
        String temporaryWordPath1 = "C:\\data1\\share\\data\\word\\SHA-EC-1908-000087-BC-00001-temp.doc";
        String temporaryWordPath2 = "C:\\data1\\share\\data\\word\\SHA-EC-1908-000087-BC-00001-temp_test.doc";
        String img = "C:\\Users\\Roy Z Zhou\\Pictures\\timg.jpg";

        boolean actionResult;
        try {
            int docPrId = new Random().nextInt(10000);
            String picName = "pic_" + docPrId;

            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(temporaryWordPath1));
            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, new File(img));
            imagePart.getRelLast().setId("rId" + docPrId);

            Anchor anchor = new Anchor();
            anchor.setAllowOverlap(true);
            anchor.setBehindDoc(false);
            anchor.setDistT(0L);
            anchor.setDistB(0L);
            anchor.setDistL(114300L);
            anchor.setDistR(114300L);
            anchor.setLayoutInCell(true);
            anchor.setLocked(false);
            anchor.setRelativeHeight(251661312);

            CTPoint2D ctPoint2D = new CTPoint2D();
            ctPoint2D.setX(0);
            ctPoint2D.setY(0);
            anchor.setSimplePos(ctPoint2D);

            CTPosH ctPosH = new CTPosH();
            ctPosH.setRelativeFrom(STRelFromH.MARGIN);
            ctPosH.setAlign(STAlignH.RIGHT);
            anchor.setPositionH(ctPosH);

            CTPosV ctPosV = new CTPosV();
            ctPosV.setRelativeFrom(STRelFromV.PARAGRAPH);
            ctPosV.setPosOffset(-1009015);
            anchor.setPositionV(ctPosV);

            CTPositiveSize2D ctPositiveSize2D = new CTPositiveSize2D();
            ctPositiveSize2D.setCx(890270);
            ctPositiveSize2D.setCy(890270);
            anchor.setExtent(ctPositiveSize2D);

            CTEffectExtent ctEffectExtent = new CTEffectExtent();
            ctEffectExtent.setB(5080);
            ctEffectExtent.setL(0);
            ctEffectExtent.setR(5080);
            ctEffectExtent.setT(0);
            anchor.setEffectExtent(ctEffectExtent);

            anchor.setWrapNone(new CTWrapNone());

            CTNonVisualDrawingProps ctNonVisualDrawingProps = new CTNonVisualDrawingProps();
            ctNonVisualDrawingProps.setId(docPrId);
            ctNonVisualDrawingProps.setName(picName);
            ctNonVisualDrawingProps.setDescr(picName);
            anchor.setDocPr(ctNonVisualDrawingProps);

            CTNonVisualGraphicFrameProperties ctNonVisualGraphicFrameProperties = new CTNonVisualGraphicFrameProperties();
            CTGraphicalObjectFrameLocking ctGraphicalObjectFrameLocking = new CTGraphicalObjectFrameLocking();
            ctGraphicalObjectFrameLocking.setNoChangeAspect(true);
            ctNonVisualGraphicFrameProperties.setGraphicFrameLocks(ctGraphicalObjectFrameLocking);
            anchor.setCNvGraphicFramePr(ctNonVisualGraphicFrameProperties);

            Pic pic = new Pic();
            // 1.nvPicPr
            CTPictureNonVisual ctPictureNonVisual = new CTPictureNonVisual();
            ctNonVisualDrawingProps.setId(docPrId);
            ctNonVisualDrawingProps.setName(picName);
            ctPictureNonVisual.setCNvPr(ctNonVisualDrawingProps);
            ctPictureNonVisual.setCNvPicPr(new CTNonVisualPictureProperties());
            pic.setNvPicPr(ctPictureNonVisual);
            // 2.blipFill
            CTBlipFillProperties ctBlipFillProperties = new CTBlipFillProperties();
            CTBlip ctBlip = new CTBlip();
            ctBlip.setEmbed(imagePart.getRelLast().getId());
            ctBlipFillProperties.setBlip(ctBlip);
            CTStretchInfoProperties ctStretchInfoProperties = new CTStretchInfoProperties();
            ctStretchInfoProperties.setFillRect(new CTRelativeRect());
            ctBlipFillProperties.setStretch(ctStretchInfoProperties);
            pic.setBlipFill(ctBlipFillProperties);
            // 3.spPr
            CTShapeProperties ctShapeProperties = new CTShapeProperties();
            CTTransform2D ctTransform2D = new CTTransform2D();
            CTPoint2D point2D = new CTPoint2D();
            point2D.setX(0);
            point2D.setY(0);
            ctTransform2D.setOff(point2D);
            ctTransform2D.setExt(ctPositiveSize2D);
            ctShapeProperties.setXfrm(ctTransform2D);
            CTPresetGeometry2D ctPresetGeometry2D = new CTPresetGeometry2D();
            ctPresetGeometry2D.setPrst(STShapeType.RECT);
            ctPresetGeometry2D.setAvLst(new CTGeomGuideList());
            ctShapeProperties.setPrstGeom(ctPresetGeometry2D);
            pic.setSpPr(ctShapeProperties);

            GraphicData graphicData = new GraphicData();
            graphicData.setUri("http://schemas.openxmlformats.org/drawingml/2006/picture");
            graphicData.getAny().add(pic);
            Graphic graphic = new Graphic();
            graphic.setGraphicData(graphicData);
            anchor.setGraphic(graphic);

            Drawing drawing = new Drawing();
            drawing.getAnchorOrInline().add(anchor);

            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
            List<Object> content = documentPart.getContent();
            if (content.size() == 0) {
                P p = new P();
                p.getContent().add(drawing);
                documentPart.addObject(p);
            } else {
                Object obj = content.get(0);
                if (obj instanceof P) {
                    P p = (P) obj;
                    p.getContent().add(drawing);
                } else {
                    P p = new P();
                    p.getContent().add(drawing);
                    content.add(0, p);
                }
            }
            wordMLPackage.save(new FileOutputStream(temporaryWordPath2));

            actionResult = true;
        } catch (org.docx4j.openpackaging.exceptions.InvalidFormatException e) {
            actionResult = false;
        } catch (Docx4JException e) {
            actionResult = false;
        } catch (Exception e) {
            actionResult = false;
        }
    }

    public static void addQRCodeByTemplateTest() {
        String originWordPath = "C:\\data1\\share\\data\\word\\SHA-EC-1908-000087-BC-00001-temp.doc";
        String generateWordPath = "C:\\data1\\share\\data\\word\\SHA-EC-1908-000087-BC-00001-temp_test.doc";
        String imageFilePath = "C:\\Users\\Roy Z Zhou\\Pictures\\1.jpeg";
        File imgFile = new File(imageFilePath);
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(originWordPath));
            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imgFile);
            int docPrId = 1;
            int cNvPrId = 2;
            Anchor anchor = createImageAnchor(imagePart, "Filename hint", "Alternative text",
                    docPrId, cNvPrId, false);
            Drawing drawing = new Drawing();
            drawing.getAnchorOrInline().add(anchor);
            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
            List<Object> content = documentPart.getContent();
            P p = (P) content.get(0);
            p.getContent().add(drawing);
            wordMLPackage.save(new FileOutputStream(generateWordPath));
        } catch (org.docx4j.openpackaging.exceptions.InvalidFormatException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        mappings.put("rEmbedId", image.getRelLast().getId());
        mappings.put("id1", Integer.toString(id1));
        mappings.put("id2", Integer.toString(id2));
        Object o = XmlUtils.unmarshallFromTemplate(ml, mappings);
        Anchor anchor = (Anchor) ((JAXBElement) o).getValue();

        return anchor;
    }

    public static void mergeTest() {
        String temporaryWordPath1 = "C:\\data1\\share\\data\\word\\1570600059124_SHA-EC-1908-000087-BC-00001.docx";
        String temporaryWordPath2 = "C:\\data1\\share\\data\\word\\1570600064910_SHA-EC-1908-000087-BC-00002.docx";
        String temporaryWordPath3 = "C:\\data1\\share\\data\\word\\1570600069946_SHA-EC-1908-000087-BC-00003.docx";
        String generateWordPath = "C:\\data1\\share\\data\\word\\merge.doc";

        List<String> fileList = new ArrayList<>();
        fileList.add(temporaryWordPath1);
        fileList.add(temporaryWordPath2);
        fileList.add(temporaryWordPath3);

        merge(fileList, generateWordPath);
    }

    public static boolean merge(List<String> fileList, String mergedFilePath) {
        if (CollectionUtils.isEmpty(fileList)) {
            System.out.println("合并word失败，合并的文件列表为空");
            return false;
        }
        // 过滤不存在的文件
        ListIterator<String> stringListIterator = fileList.listIterator();
        while (stringListIterator.hasNext()) {
            String filePath = stringListIterator.next();
            File file = new File(filePath);
            if (!file.exists()) {
                fileList.remove(filePath);
            }
        }
        if (CollectionUtils.isEmpty(fileList)) {
            System.out.println("合并word失败，过滤后需要合并的文件列表为空");
            return false;
        }

        try {
            File file = new File(fileList.get(1));

            Drawing drawing = null;
            XWPFDocument document = new XWPFDocument(new FileInputStream(file));
            XWPFParagraph paragraph = document.getParagraphs().get(0);
            CTP ctp = paragraph.getCTP();
            Node domNode = ctp.getDomNode();
            NodeList childNodes = domNode.getChildNodes();
            int length = childNodes.getLength();
            for (int i = 0; i < length; i++) {
                Node node = childNodes.item(i);
                String prefix = node.getPrefix();
                String nodeName = node.getNodeName();
                if (nodeName.equals("w:drawing")) {
                    try {
                        drawing = (Drawing) XmlUtils.unmarshal(node);
                    } catch (JAXBException e) {
                        e.printStackTrace();
                    }
                }
            }

            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(file);
            MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
            if (fileList.size() > 1) {
                for (int i = 1; i < fileList.size(); i++) {
                    // 插入分隔符
                    Br br = new Br();
                    br.setType(STBrType.PAGE);
                    br.setClear(STBrClear.LEFT);
                    mainDocumentPart.addObject(br);

                    PartName partName = new PartName("/part_" + i + ".docx");
                    System.out.println("part：" + partName.getName());
                    AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(partName);
                    File docFile = new File(fileList.get(i));
                    afiPart.setBinaryData(new FileInputStream(docFile));
                    Relationship altChunkRel = mainDocumentPart.addTargetPart(afiPart);
                    CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
                    chunk.setId(altChunkRel.getId());

                    mainDocumentPart.addObject(chunk);
                }
                if (null != drawing && mainDocumentPart.getContent().size() > 0) {
                    List<Object> content = mainDocumentPart.getContent();
                    Object obj = content.get(0);
                    if (obj instanceof P) {
                        P p = (P) obj;
                        p.getContent().add(drawing);
                    } else {
                        P p = new P();
                        p.getContent().add(drawing);
                        content.add(0, p);
                    }
                }
            }
            File mergeFile = new File(mergedFilePath);
            File parentFile = mergeFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            wordprocessingMLPackage.save(mergeFile);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }

        return false;
    }

}
