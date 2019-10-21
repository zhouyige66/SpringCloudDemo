package cn.roy.springcloud.api2;

import io.swagger.models.auth.In;
import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.wml.SectPr;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.impl.CTSectPrImpl;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import static org.openxmlformats.schemas.wordprocessingml.x2006.main.STHdrFtr.DEFAULT;

/**
 * @Description: poi测试
 * @Author: Roy Z
 * @Date: 2019-10-15 10:08
 * @Version: v1.0
 */
public class PoiWordTest {

    public static void main(String[] args) {
        String[] paths = {
                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AP-1.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AP-2.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AP-3.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AP-4.doc",
                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AR-1.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AR-2.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AR-3.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AR-4.doc",
                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\EIC-1.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\EIC-2.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\EIC-3.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\EIC-4.doc"
        };

        String generateWordPath = "C:\\Users\\Roy Z Zhou\\Desktop\\word\\merge.doc";
        List<String> fileList = Arrays.asList(paths);
        long startTime = System.currentTimeMillis();
        mergeByPoi(fileList, generateWordPath);
        System.out.println("耗时：" + ((System.currentTimeMillis() - startTime) / 1000) + "S");
    }

    public static boolean mergeByPoi(List<String> fileList, String mergedFilePath) {
        fileList = preProcessFileList(fileList);
        if (CollectionUtils.isEmpty(fileList)) {
            return false;
        }

        try {
            File file = new File(fileList.get(0));
            XWPFDocument mainDocument = new XWPFDocument(new FileInputStream(file));

            if (fileList.size() > 1) {
                List<XWPFDocument> documentList = new ArrayList<>();
                // 分节符列表
                List<CTSectPr> ctSectPrList = new ArrayList<>();
                // 样式
                XWPFStyles mainStyles = mainDocument.getStyles();
                /**
                 * 分节符以及样式处理
                 */
                for (int i = 0; i < fileList.size(); i++) {
                    XWPFDocument document;
                    if (i == 0) {
                        document = mainDocument;
                    } else {
                        document = new XWPFDocument(new FileInputStream(new File(fileList.get(i))));
                    }
                    documentList.add(document);

                    // 设置页码
                    List<XWPFFooter> footerList = document.getFooterList();
                    XWPFFooter footer = footerList.get(0);
                    CTHdrFtr ctHdrFtr = footer._getHdrFtr();
                    String s = ctHdrFtr.xmlText();
                    s = s.replace("NUMPAGES", "SECTIONPAGES");
                    CTHdrFtr parse = CTHdrFtr.Factory.parse(s);
                    footer.setHeaderFooter(parse);

                    String relationId = "";
                    if (i == 0) {
                        relationId = mainDocument.getRelationId(footer);
                    } else {
                        XWPFStyles itemStyles = document.getStyles();
                        CTStyles itemCtStyles = document.getStyle();
                        List<CTStyle> itemStyleList = itemCtStyles.getStyleList();
                        for (CTStyle ctStyle : itemStyleList) {
                            String styleId = ctStyle.getStyleId();
                            XWPFStyle style = itemStyles.getStyle(styleId);
                            mainStyles.addStyle(style);
                        }

                        List<POIXMLDocumentPart.RelationPart> relationPartList = document.getRelationParts();
                        for (POIXMLDocumentPart.RelationPart relationPart : relationPartList) {
                            PackageRelationship relationship = relationPart.getRelationship();
                            String relationshipType = relationship.getRelationshipType();
                            PackagePart source = relationship.getSource();

                            POIXMLDocumentPart poixmlDocumentPart = relationPart.getDocumentPart();

                            relationPart.getDocumentPart();
                            if (relationshipType.equals(Namespaces.FONT_TABLE)) {
                                POIXMLDocumentPart documentPart = relationPart.getDocumentPart();
                                String rId = "rId" + (mainDocument.getRelationParts().size() + 1);
                                XWPFRelation instance = XWPFRelation.getInstance(relationshipType);
                                mainDocument.addRelation(rId, instance, documentPart);
                            }
                        }

                        XWPFRelation relation = XWPFRelation.FOOTER;
                        int i1 = mainDocument.getRelations().size() + 1;
                        XWPFFooter xwpfFooter = (XWPFFooter) mainDocument.createRelationship(relation,
                                XWPFFactory.getInstance(), i1);
                        xwpfFooter.setHeaderFooter(parse);
                        relationId = mainDocument.getRelationId(xwpfFooter);
                    }
                    // 创建footer关联关系
                    CTHdrFtrRef[] ctHdrFtrRefs = new CTHdrFtrRef[1];
                    CTHdrFtrRef ctHdrFtrRef = CTHdrFtrRef.Factory.newInstance();
                    ctHdrFtrRef.setType(DEFAULT);
                    ctHdrFtrRef.setId(relationId);
                    ctHdrFtrRefs[0] = ctHdrFtrRef;

                    // 拿到本document的分隔符
                    CTBody body = document.getDocument().getBody();
                    CTSectPr sectPr = body.getSectPr();
                    byte[] rsidSect = sectPr.getRsidSect();
                    // 找到第一个分节符
                    CTSectPr firstSec = findFirstCTSectPr(document);
                    if (firstSec == null) {
                        System.out.println("未找到分节符");
                        firstSec = (CTSectPr) sectPr.copy();
                    }
                    byte[] rsidSect2 = firstSec.getRsidSect();
                    CTSectPr copySec = (CTSectPr) firstSec.copy();
                    copySec.setRsidR(rsidSect2);
                    copySec.setRsidSect(rsidSect);
                    copySec.setFooterReferenceArray(ctHdrFtrRefs);
                    setSectionTypeAndPgNumberType(copySec);

                    ctSectPrList.add(copySec);
                }

                replaceSection(mainDocument);
                removeBodySection(mainDocument);
                addPageBreak(mainDocument, ctSectPrList.get(0));
                for (int i = 1; i < documentList.size(); i++) {
                    XWPFDocument document = documentList.get(i);
                    replaceSection(document);
                    if (i == documentList.size() - 1) {
                        document.removeBodyElement(document.getBodyElements().size() - 2);
                        CTSectPr sectPr = document.getDocument().getBody().getSectPr();
                        setSectionTypeAndPgNumberType(sectPr);
                    }else {
                        removeBodySection(document);
                        addPageBreak(document, ctSectPrList.get(i));
                    }

                    // 拼接
                    appendBody(mainDocument, document);
                }
            }

            File mergeFile = new File(mergedFilePath);
            File parentFile = mergeFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            mainDocument.write(new FileOutputStream(mergeFile));

            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static List<String> preProcessFileList(List<String> fileList) {
        if (CollectionUtils.isEmpty(fileList)) {
            return null;
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
            return null;
        }
        return fileList;
    }

    private static CTSectPr findFirstCTSectPr(XWPFDocument document) {
        CTP[] pArray = document.getDocument().getBody().getPArray();
        CTSectPr sectPr = null;
        for (CTP ctp : pArray) {
            String ctpStr = ctp.xmlText();
            if (ctpStr.contains("w:sectPr")) {
                CTPPr pPr = ctp.getPPr();
                CTSectPr sectPr1 = pPr.getSectPr();
                CTSectType type = sectPr1.getType();
                if (type != null) {
                    System.out.println("类型是：" + type.getVal());
                    sectPr = sectPr1;
                    break;
                }
            }
        }
        return sectPr;
    }

    private static void setSectionTypeAndPgNumberType(CTSectPr sectPr){
        CTSectType type = sectPr.getType();
        if (type == null) {
            type = sectPr.addNewType();
        }
        type.setVal(STSectionMark.NEXT_PAGE);
        CTPageNumber ctPageNumber = sectPr.getPgNumType();
        if (ctPageNumber == null) {
            ctPageNumber = sectPr.addNewPgNumType();
        }
        ctPageNumber.setStart(BigInteger.valueOf(1));
    }

    private static void replaceSection(XWPFDocument document) {
        CTP[] pArray = document.getDocument().getBody().getPArray();
        int index = 0;
        List<Integer> remove = new ArrayList<>();
        for (CTP ctp : pArray) {
            String ctpStr = ctp.xmlText();
            if (ctpStr.contains("w:sectPr")) {
                System.out.println("提取的节：" + ctp.xmlText());
                remove.add(index);
            }
            index++;
        }
        System.out.println("删除行：" + remove);
        List<Integer> list = new ArrayList<>();
        for (Integer i : remove) {
            XWPFParagraph paragraphArray = document.getParagraphArray(i);
            document.getParagraph(paragraphArray.getCTP());
            int paragraphPos = document.getPosOfParagraph(paragraphArray);
            System.out.println("查询的pos:" + paragraphPos);
            list.add(paragraphPos);
        }
        System.out.println("删除前长度：" + document.getBodyElements().size());
        Collections.reverse(list);
        for (Integer i : list) {
            document.removeBodyElement(i);
        }
        System.out.println("删除后长度：" + document.getBodyElements().size());
    }

    private static void removeBodySection(XWPFDocument document) {
        NodeList nodes = document.getDocument().getBody().getDomNode().getChildNodes();
        Node node = nodes.item(nodes.getLength() - 1);
        document.getDocument().getBody().getDomNode().removeChild(node);
    }

    private static void addPageBreak(XWPFDocument document, CTSectPr sectPr) {
        System.out.println("***************添加分节符开始***************");
        List<IBodyElement> bodyElements = document.getBodyElements();
        int size = bodyElements.size();
        XWPFParagraph paragraph = null;
        if (size > 0) {
            IBodyElement element = bodyElements.get(size - 1);
            if (element instanceof XWPFParagraph) {
                paragraph = (XWPFParagraph) element;
            }
        }
        if (paragraph == null) {
            System.out.println("最后一个元素不是段落，新增一个段落");
            paragraph = document.createParagraph();
        }

        CTP ctp = paragraph.getCTP();
        System.out.println("段落添加前：" + ctp.xmlText());
        CTPPr pPr = ctp.getPPr();
        if (pPr == null) {
            pPr = ctp.addNewPPr();
        }
        pPr.setSectPr(sectPr);
        System.out.println("段落添加后：" + ctp.xmlText());
        System.out.println("***************添加分节符结束***************");
    }

    private static void appendBody(XWPFDocument src, XWPFDocument append) throws Exception {
        CTBody srcBody = src.getDocument().getBody();
        CTBody appendBody = append.getDocument().getBody();

        List<XWPFPictureData> allPictures = append.getAllPictures();
        // 记录图片合并前及合并后的ID
        Map<String, String> map = new HashMap();
        for (XWPFPictureData picture : allPictures) {
            String before = append.getRelationId(picture);
            //将原文档中的图片加入到目标文档中
            String after = src.addPictureData(picture.getData(), picture.getPictureType());
            map.put(before, after);
        }

        XmlOptions optionsOuter = new XmlOptions();
        optionsOuter.setSaveOuter();
        String appendString = appendBody.xmlText(optionsOuter);

        String srcString = srcBody.xmlText();
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

        srcBody.set(makeBody);
    }

    private static void addQRCode2Word() {
        String docFilePath = "C:\\Users\\Roy Z Zhou\\Desktop\\fsdownload\\SHA-EC-1907-001873-EIC-00001-temp.doc";
        String imgFilePath = "C:\\Users\\Roy Z Zhou\\Pictures\\1.jpeg";
        String generateFilePath = "C:\\Users\\Roy Z Zhou\\Desktop\\fsdownload\\SHA-EC-1907-001873-EIC-00001-temp_test.doc";
        File file = new File(docFilePath);
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(new FileInputStream(file));
            CTBody body = document.getDocument().getBody();
            CTP[] pArray = body.getPArray();
            boolean needAdd = false;
            CTP ctp;
            if (pArray.length > 0) {
                ctp = pArray[0];
            } else {
                ctp = document.createParagraph().getCTP();
                needAdd = true;
            }

            XWPFParagraph paragraph = document.getParagraph(ctp);
            XWPFRun run = paragraph.createRun();
            CTDrawing ctDrawing = run.getCTR().addNewDrawing();

            String relationId = document.addPictureData(new FileInputStream(new File(imgFilePath)),
                    Document.PICTURE_TYPE_JPEG);
            XWPFPictureData picData = (XWPFPictureData) document.getRelationById(relationId);
            String fileName = picData.getFileName();
            String rEmbedId = paragraph.getPart().getRelationId(picData);
            int picId = new Random().nextInt(10000);
            HashMap<String, String> map = new HashMap<>();
            map.put("rEmbedId", rEmbedId);
            map.put("docName", "Picture " + picId);
            map.put("picId", Integer.toString(picId));
            map.put("picName", fileName);
            String drawingStr = replace(map);

            CTAnchor parse = CTAnchor.Factory.parse(drawingStr);
            ctDrawing.set(parse);

//            XmlOptions optionsOuter = new XmlOptions();
//            optionsOuter.setSaveOuter();
//            String pString = ctp.xmlText(optionsOuter);
//            String replace = pString.replace("<w:drawing/>", drawingStr);
//            CTP newCtp = CTP.Factory.parse(replace);
//            ctp.set(newCtp);

            File mergeFile = new File(generateFilePath);
            File parentFile = mergeFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            document.write(new FileOutputStream(mergeFile));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
    }

    private static String replace(HashMap<String, String> map) {
        String template = "<wp:anchor xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n" +
                "           xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n" +
                "           xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\"\n" +
                "                   distT=\"0\" distB=\"0\" distL=\"114300\" distR=\"114300\" simplePos=\"0\"\n" +
                "                   relativeHeight=\"251658240\" behindDoc=\"0\" locked=\"0\" layoutInCell=\"1\"\n" +
                "                   allowOverlap=\"1\">\n" +
                "            <wp:simplePos x=\"0\" y=\"0\"/>\n" +
                "            <wp:positionH relativeFrom=\"margin\">\n" +
                "                <wp:align>right</wp:align>\n" +
                "            </wp:positionH>\n" +
                "            <wp:positionV relativeFrom=\"paragraph\">\n" +
                "                <wp:posOffset>-819398</wp:posOffset>\n" +
                "            </wp:positionV>\n" +
                "            <wp:extent cx=\"818985\" cy=\"818985\"/>\n" +
                "            <wp:effectExtent l=\"0\" t=\"0\" r=\"635\" b=\"635\"/>\n" +
                "            <wp:wrapNone/>\n" +
                "            <wp:docPr id=\"${picId}\" name=\"${docName}\"/>\n" +
                "            <wp:cNvGraphicFramePr>\n" +
                "                <a:graphicFrameLocks\n" +
                "                        xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n" +
                "                        noChangeAspect=\"1\"/>\n" +
                "            </wp:cNvGraphicFramePr>\n" +
                "            <a:graphic xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\">\n" +
                "                <a:graphicData uri=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                "                    <pic:pic\n" +
                "                            xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\">\n" +
                "                        <pic:nvPicPr>\n" +
                "                            <pic:cNvPr id=\"${picId}\" name=\"${picName}\"/>\n" +
                "                            <pic:cNvPicPr/>\n" +
                "                        </pic:nvPicPr>\n" +
                "                        <pic:blipFill>\n" +
                "                            <a:blip r:embed=\"${rEmbedId}\" cstate=\"print\">\n" +
                "                                <a:extLst>\n" +
                "                                    <a:ext>\n" +
                "                                        <a14:useLocalDpi\n" +
                "                                                xmlns:a14=\"http://schemas.microsoft.com/office/drawing/2010/main\"\n" +
                "                                                val=\"0\"/>\n" +
                "                                    </a:ext>\n" +
                "                                </a:extLst>\n" +
                "                            </a:blip>\n" +
                "                            <a:stretch>\n" +
                "                                <a:fillRect/>\n" +
                "                            </a:stretch>\n" +
                "                        </pic:blipFill>\n" +
                "                        <pic:spPr>\n" +
                "                            <a:xfrm flipH=\"1\">\n" +
                "                                <a:off x=\"0\" y=\"0\"/>\n" +
                "                                <a:ext cx=\"818985\" cy=\"818985\"/>\n" +
                "                            </a:xfrm>\n" +
                "                            <a:prstGeom prst=\"rect\">\n" +
                "                                <a:avLst/>\n" +
                "                            </a:prstGeom>\n" +
                "                        </pic:spPr>\n" +
                "                    </pic:pic>\n" +
                "                </a:graphicData>\n" +
                "            </a:graphic>\n" +
                "        </wp:anchor>";
        Set<Map.Entry<String, String>> entries = map.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            template = template.replace("${" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }

}
