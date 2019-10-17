package cn.roy.springcloud.api2;

import org.apache.commons.collections4.MapUtils;
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
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
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
        addImageByTemplateTest();
    }

    private static void addImageByCode() {
        String temporaryWordPath1 = "C:\\data1\\share\\data\\word\\SHA-EC-1907-001873-EIC-00001-temp.doc";
        String temporaryWordPath2 = "C:\\data1\\share\\data\\word\\qrcode.doc";
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
            anchor.setLayoutInCell(false);
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

    private static void addImageByTemplateTest() {
        String originWordPath = "C:\\data1\\share\\data\\word\\SHA-EC-1907-001873-EIC-00001-temp.doc";
        String generateWordPath = "C:\\data1\\share\\data\\word\\SHA-EC-1907-001873-EIC-00001-temp_test.doc";
        String imageFilePath = "C:\\Users\\Roy Z Zhou\\Pictures\\1.jpeg";
        File imgFile = new File(imageFilePath);
        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(originWordPath));
            MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
            List<Object> content = documentPart.getContent();

            // 获取所有表格
//            List<JAXBElement> ctTableRemoveList = new ArrayList<>();
//            TreeMap<Integer, Object> ctTableAddMap = new TreeMap<>();
//            int index = 0;
//            for (Object obj : content) {
//                if (obj instanceof JAXBElement) {
//                    JAXBElement element = (JAXBElement) obj;
//                    Object value = element.getValue();
//                    if (value instanceof Tbl) {
//                        ctTableRemoveList.add(element);
//
//                        Tbl originTbl = (Tbl) value;
//                        String s = XmlUtils.marshaltoString(originTbl);
//                        System.out.println("原表格："+s);
//                        // 拷贝当前表格
//                        Tbl tbl = XmlUtils.deepCopy((Tbl) value);
//                        ctTableAddMap.put(index, tbl);
//                    }
//                }
//                index++;
//            }

//            boolean removeResult = content.removeAll(ctTableRemoveList);
//            System.out.println("移除表格：" + removeResult);
            // 添加二维码
            BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage, imgFile);
            int docPrId = 1;
            int cNvPrId = 2;
            Anchor anchor = createImageAnchor(imagePart, "Filename hint", "Alternative text",
                    docPrId, cNvPrId, false);
            Drawing drawing = new Drawing();
            drawing.getAnchorOrInline().add(anchor);
            if (content.size() == 0) {
                P p = new P();
                p.getContent().add(drawing);
                documentPart.addObject(p);
            } else {
                Object obj = content.get(0);
                if (obj instanceof P) {
                    P p = (P) obj;
                    p.getContent().add(0, drawing);
                } else {
                    P p = new P();
                    p.getContent().add(drawing);
                    content.add(0, p);
                }
            }

            // 添加原来的表格
//            if (MapUtils.isNotEmpty(ctTableAddMap)) {
//                Set<Map.Entry<Integer, Object>> entries = ctTableAddMap.entrySet();
//                for (Map.Entry<Integer, Object> entry : entries) {
//                    content.add(entry.getKey(), entry.getValue());
//                }
//            }

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

        String a =
                "<wp:anchor xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n" +
                        "           xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n" +
                        "           xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\"\n" +
                        "           distT=\"0\" distB=\"0\" distL=\"114300\" distR=\"114300\" simplePos=\"0\"\n" +
                        "           relativeHeight=\"251658240\" behindDoc=\"0\" locked=\"0\" layoutInCell=\"0 \"\n" +
                        "           allowOverlap=\"1\">\n" +
                        "    <wp:simplePos x=\"0\" y=\"0\"/>\n" +
                        "    <wp:positionH relativeFrom=\"margin\">\n" +
                        "        <wp:posOffset>5577840</wp:posOffset>\n" +
                        "    </wp:positionH>\n" +
                        "    <wp:positionV relativeFrom=\"topMargin\">\n" +
                        "        <wp:posOffset>548640</wp:posOffset>\n" +
                        "    </wp:positionV>\n" +
                        "    <wp:extent cx=\"${cx}\" cy=\"${cy}\"/>\n" +
                        "    <wp:effectExtent l=\"0\" t=\"0\" r=\"8890\" b=\"8890\"/>\n" +
                        "    <wp:wrapNone/>\n" +
                        "    <wp:docPr id=\"${docPrId}\" name=\"${docPrName}\" descr=\"${docPrDes}\"/>\n" +
                        "    <wp:cNvGraphicFramePr>\n" +
                        "        <a:graphicFrameLocks\n" +
                        "                xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n" +
                        "                noChangeAspect=\"1\"/>\n" +
                        "    </wp:cNvGraphicFramePr>\n" +
                        "    <a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">\n" +
                        "        <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                        "            <pic:pic\n" +
                        "                    xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                        "                <pic:nvPicPr>\n" +
                        "                    <pic:cNvPr id=\"${docPrId}\" name=\"${docPrName}\"/>\n" +
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

        cx = cy = 800000L;
        String xml = "<wp:anchor distT=\"0\" distB=\"0\" distL=\"114300\" distR=\"114300\" simplePos=\"0\" relativeHeight=\"251658240\" behindDoc=\"0\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\">\n" +
                "    <wp:simplePos x=\"0\" y=\"0\"/>\n" +
                "    <wp:positionH relativeFrom=\"margin\">\n" +
                "        <wp:align>right</wp:align>\n" +
                "    </wp:positionH>\n" +
                "    <wp:positionV relativeFrom=\"topMargin\">\n" +
                "        <wp:align>bottom</wp:align>\n" +
                "    </wp:positionV>\n" +
                "    <wp:extent cx=\"${cx}\" cy=\"${cy}\"/>\n" +
                "    <wp:effectExtent l=\"0\" t=\"0\" r=\"8890\" b=\"8890\"/>\n" +
                "    <wp:wrapNone/>\n" +
                "    <wp:docPr descr=\"${docPrDes}\" id=\"${docPrId}\" name=\"${docPrName}\"/>\n" +
                "    <wp:cNvGraphicFramePr>\n" +
                "        <a:graphicFrameLocks noChangeAspect=\"1\" xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"/>\n" +
                "    </wp:cNvGraphicFramePr>\n" +
                "    <a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">\n" +
                "        <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                "            <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                "                <pic:nvPicPr>\n" +
                "                    <pic:cNvPr id=\"${picId}\" name=\"${picName}\"/>\n" +
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
        String ml =
                "<wp:anchor distT=\"0\" distB=\"0\" distL=\"114300\" distR=\"114300\" simplePos=\"0\" relativeHeight=\"251658240\" behindDoc=\"0\" locked=\"0\" layoutInCell=\"1\" allowOverlap=\"1\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\" xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\">\n" +
                        "    <wp:simplePos x=\"0\" y=\"0\"/>\n" +
                        "    <wp:positionH relativeFrom=\"margin\">\n" +
                        "        <wp:align>right</wp:align>\n" +
                        "    </wp:positionH>\n" +
                        "    <wp:positionV relativeFrom=\"topMargin\">\n" +
                        "        <wp:align>bottom</wp:align>\n" +
                        "    </wp:positionV>\n" +
                        "    <wp:extent cx=\"886968\" cy=\"886968\"/>\n" +
                        "    <wp:effectExtent l=\"0\" t=\"0\" r=\"8890\" b=\"8890\"/>\n" +
                        "    <wp:wrapNone/>\n" +
                        "    <wp:docPr id=\"2\" name=\"Picture 2\"/>\n" +
                        "    <wp:cNvGraphicFramePr>\n" +
                        "        <a:graphicFrameLocks xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n" +
                        "                             noChangeAspect=\"1\"/>\n" +
                        "    </wp:cNvGraphicFramePr>\n" +
                        "    <a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">\n" +
                        "        <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                        "            <pic:pic xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                        "                <pic:nvPicPr>\n" +
                        "                    <pic:cNvPr id=\"2\" name=\"timg.jpg\"/>\n" +
                        "                    <pic:cNvPicPr/>\n" +
                        "                </pic:nvPicPr>\n" +
                        "                <pic:blipFill>\n" +
                        "                    <a:blip r:embed=\"${rEmbedId}\" cstate=\"print\">\n" +
                        "                        <a:extLst>\n" +
                        "                            <a:ext uri=\"{28A0092B-C50C-407E-A947-70E740481C1C}\">\n" +
                        "                                <a14:useLocalDpi\n" +
                        "                                        xmlns:a14=\"http://schemas.microsoft.com/office/drawing/2010/main\"\n" +
                        "                                        val=\"0\"/>\n" +
                        "                            </a:ext>\n" +
                        "                        </a:extLst>\n" +
                        "                    </a:blip>\n" +
                        "                    <a:stretch>\n" +
                        "                        <a:fillRect/>\n" +
                        "                    </a:stretch>\n" +
                        "                </pic:blipFill>\n" +
                        "                <pic:spPr>\n" +
                        "                    <a:xfrm>\n" +
                        "                        <a:off x=\"0\" y=\"0\"/>\n" +
                        "                        <a:ext cx=\"886968\" cy=\"886968\"/>\n" +
                        "                    </a:xfrm>\n" +
                        "                    <a:prstGeom prst=\"rect\">\n" +
                        "                        <a:avLst/>\n" +
                        "                    </a:prstGeom>\n" +
                        "                </pic:spPr>\n" +
                        "            </pic:pic>\n" +
                        "        </a:graphicData>\n" +
                        "    </a:graphic>\n" +
                        "    <wp14:sizeRelH relativeFrom=\"margin\">\n" +
                        "        <wp14:pctWidth>0</wp14:pctWidth>\n" +
                        "    </wp14:sizeRelH>\n" +
                        "    <wp14:sizeRelV relativeFrom=\"margin\">\n" +
                        "        <wp14:pctHeight>0</wp14:pctHeight>\n" +
                        "    </wp14:sizeRelV>\n" +
                        "</wp:anchor>";
        HashMap<String, String> mappings = new HashMap();
//        mappings.put("cx", Long.toString(cx));
//        mappings.put("cy", Long.toString(cy));
//        mappings.put("filenameHint", filenameHint);
//        mappings.put("altText", altText);
//        mappings.put("rEmbedId", image.getRelLast().getId());
//        mappings.put("id1", Integer.toString(id1));
//        mappings.put("id2", Integer.toString(id2));

        mappings.put("cx", Long.toString(cx));
        mappings.put("cy", Long.toString(cy));
        int docPrId = 100;
        mappings.put("docPrId", "" + docPrId);
        mappings.put("docPrName", "docPrName");
        mappings.put("docPrDes", "docPrDes");
        mappings.put("rEmbedId", image.getRelLast().getId());

        Object o = XmlUtils.unmarshallFromTemplate(a, mappings);
        Anchor anchor = (Anchor) ((JAXBElement) o).getValue();

        return anchor;
    }

    public static void mergeTest() {
        String[] paths = {
//                "C:\\data1\\share\\data\\word\\AP1.doc",
//                "C:\\data1\\share\\data\\word\\AR1.doc",
//                "C:\\data1\\share\\data\\word\\AR2.doc",
//                "C:\\data1\\share\\data\\word\\AR3.doc",
                "C:\\data1\\share\\data\\word\\BC1.doc",
                "C:\\data1\\share\\data\\word\\CC1.doc",
//                "C:\\data1\\share\\data\\word\\EC1.doc",
//                "C:\\data1\\share\\data\\word\\FB1.doc",
//                "C:\\data1\\share\\data\\word\\FC1.doc",
//                "C:\\data1\\share\\data\\word\\IC1.doc",
                "C:\\data1\\share\\data\\word\\IH1.doc"
        };

        String generateWordPath = "C:\\data1\\share\\data\\word\\merge.doc";
        List<String> fileList = Arrays.asList(paths);
        long startTime = System.currentTimeMillis();
        merge(fileList, generateWordPath);
        System.out.println("耗时：" + ((System.currentTimeMillis() - startTime) / 1000) + "S");
    }

    public static boolean merge(List<String> fileList, String mergedFilePath) {
        if (CollectionUtils.isEmpty(fileList)) {
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
            return false;
        }

        try {
            File file = new File(fileList.get(0));
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.load(file);
            if (fileList.size() > 1) {
                MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
                // 处理首页二维码
                Drawing drawing = null;
                XWPFDocument document = new XWPFDocument(new FileInputStream(file));
                XWPFParagraph paragraph = document.getParagraphs().get(0);
                CTP ctp = paragraph.getCTP();
                Node domNode = ctp.getDomNode();
                NodeList childNodes = domNode.getChildNodes();
                int length = childNodes.getLength();
                for (int i = 0; i < length; i++) {
                    Node node = childNodes.item(i);
                    String nodeName = node.getNodeName();
                    if (nodeName.equals("w:drawing")) {
                        try {
                            drawing = (Drawing) XmlUtils.unmarshal(node);
                        } catch (JAXBException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // 在当前文件后追加word
                for (int i = 1; i < fileList.size(); i++) {
                    // 插入分隔符
                    Br br = new Br();
                    br.setType(STBrType.PAGE);
                    br.setClear(STBrClear.LEFT);
                    mainDocumentPart.addObject(br);

                    PartName partName = new PartName("/part_" + i + ".docx");
                    System.out.println("part：" + partName.getName());
                    AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(partName);
                    afiPart.setAltChunkType(AltChunkType.WordprocessingML);
                    File docFile = new File(fileList.get(i));
                    afiPart.setBinaryData(new FileInputStream(docFile));
                    Relationship altChunkRel = mainDocumentPart.addTargetPart(afiPart);
                    altChunkRel.setTargetMode("Internal");
                    CTAltChunk ctAltChunk = Context.getWmlObjectFactory().createCTAltChunk();
                    CTAltChunkPr ctAltChunkPr = new CTAltChunkPr();
                    BooleanDefaultTrue booleanDefaultTrue = new BooleanDefaultTrue();
                    booleanDefaultTrue.setVal(true);
                    ctAltChunkPr.setMatchSrc(booleanDefaultTrue);
                    ctAltChunk.setAltChunkPr(ctAltChunkPr);
                    ctAltChunk.setId(altChunkRel.getId());

                    mainDocumentPart.addObject(ctAltChunk);
                }
                // docx4j识别不出drawing，所以需要对首页二维码做重新插入操作
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

    private static void appendImage(CTBody src, String appendString, Map<String, String> map) throws Exception {
        String srcString = src.xmlText();
        String prefix = srcString.substring(0, srcString.indexOf(">") + 1);
        String mainPart = srcString.substring(srcString.indexOf(">") + 1, srcString.lastIndexOf("<"));
        String sufix = srcString.substring(srcString.lastIndexOf("<"));
        String addPart = appendString.substring(appendString.indexOf(">") + 1, appendString.lastIndexOf("<"));

        if (map != null && !map.isEmpty()) {
            //对xml字符串中图片ID进行替换
            for (Map.Entry<String, String> set : map.entrySet()) {
                addPart = addPart.replace(set.getKey(), set.getValue());
            }
        }
        //将两个文档的xml内容进行拼接
        CTBody makeBody = CTBody.Factory.parse(prefix + mainPart + addPart + sufix);

        src.set(makeBody);
    }

}