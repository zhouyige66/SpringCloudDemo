package cn.roy.springcloud.api2;

import io.swagger.models.auth.In;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.docx4j.wml.SectPr;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.util.CollectionUtils;

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
                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AR-1.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AR-2.doc",
                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AR-3.doc",
////                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AR-4.doc",
                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AP-1.doc",
////                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AP-2.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AP-3.doc",
////                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\AP-4.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\EIC-1.doc",
////                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\EIC-2.doc",
//                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\EIC-3.doc",
////                "C:\\Users\\Roy Z Zhou\\Desktop\\word\\EIC-4.doc"
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
                // 更改footer样式
                XWPFFooter mainFooter = null;
                List<XWPFFooter> footerList = mainDocument.getFooterList();
                if (fileList.size() > 0) {
                    mainFooter = footerList.get(0);
                    CTHdrFtr ctHdrFtr = mainFooter._getHdrFtr();
                    String s = ctHdrFtr.xmlText();
                    s = s.replace("NUMPAGES", "SECTIONPAGES");
                    CTHdrFtr parse = CTHdrFtr.Factory.parse(s);
                    mainFooter.setHeaderFooter(parse);
                }
                // 创建footer关联关系
                CTHdrFtrRef[] ctHdrFtrRefs = new CTHdrFtrRef[1];
                CTHdrFtrRef ctHdrFtrRef = CTHdrFtrRef.Factory.newInstance();
                ctHdrFtrRef.setType(DEFAULT);
                ctHdrFtrRef.setId(mainDocument.getRelationId(mainFooter));
                ctHdrFtrRefs[0] = ctHdrFtrRef;
                // 替换与添加
                int top = replaceSection(mainDocument);
                CTSectPr sectPr = copyCTSectPr(mainDocument, ctHdrFtrRefs);
                if (top != -1) {
                    sectPr.getPgMar().setTop(BigInteger.valueOf(top));
                }
                XWPFParagraph paragraph = mainDocument.createParagraph();
                CTPPr ctpPr = paragraph.getCTP().addNewPPr();
                ctpPr.setSectPr(sectPr);
                for (int i = 1; i < fileList.size(); i++) {
                    XWPFDocument itemDoc = new XWPFDocument(new FileInputStream(new File(fileList.get(i))));
                    int top1 = replaceSection(itemDoc);
                    CTSectPr sectPr1 = copyCTSectPr(itemDoc, ctHdrFtrRefs);
                    if (top1 != -1) {
                        sectPr1.getPgMar().setTop(BigInteger.valueOf(top1));
                    }
                    XWPFParagraph paragraph1 = itemDoc.createParagraph();
                    paragraph1.setPageBreak(true);
                    CTPPr ctpPr1 = paragraph1.getCTP().addNewPPr();
                    ctpPr1.setSectPr(sectPr1);

                    itemDoc.removeBodyElement(itemDoc.getBodyElements().size()-2);
//                    itemDoc.getParagraphs().get(0).setPageBreak(true);
//                    CTSectPr sectPr1 = itemDoc.getDocument().getBody().getSectPr();
//                    CTSectType type = sectPr1.getType();
//                    if (type == null) {
//                        type = sectPr1.addNewType();
//                    }
//                    type.setVal(STSectionMark.NEXT_PAGE);

                    // 拼接
                    appendBody(mainDocument, itemDoc);
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

    private static int replaceSection(XWPFDocument document) {
        int top = -1;
        CTP[] pArray = document.getDocument().getBody().getPArray();
        int index = 0;
        List<Integer> remove = new ArrayList<>();
        for (CTP ctp : pArray) {
            String ctpStr = ctp.xmlText();
            if (ctpStr.contains("w:sectPr")) {
                System.out.println("提取的节：" + ctp.xmlText());
                CTPPr pPr = ctp.getPPr();
                CTSectPr sectPr = pPr.getSectPr();
                CTPageMar pgMar = sectPr.getPgMar();
                top = pgMar.getTop().intValue();
                System.out.println("提取的高度：" + top);

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

        return top;
    }

    private static CTSectPr copyCTSectPr(XWPFDocument document, CTHdrFtrRef[] ctHdrFtrRefs) {
        CTSectPr sectPr = document.getDocument().getBody().getSectPr();
        CTSectPr sectPrCopy = (CTSectPr) sectPr.copy();
        sectPrCopy.setFooterReferenceArray(ctHdrFtrRefs);
        CTSectType type = sectPrCopy.getType();
        if (type == null) {
            type = sectPrCopy.addNewType();
        }
        type.setVal(STSectionMark.NEXT_PAGE);
        CTPageNumber ctPageNumber = sectPrCopy.getPgNumType();
        if (ctPageNumber == null) {
            ctPageNumber = sectPrCopy.addNewPgNumType();
        }
        ctPageNumber.setStart(BigInteger.valueOf(1));
        return sectPrCopy;
    }

    /**
     * 预处理文件列表
     *
     * @param fileList
     * @return
     */
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

    /**
     * 拼装
     *
     * @param src
     * @param append
     * @throws Exception
     */
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
