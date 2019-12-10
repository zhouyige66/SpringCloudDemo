package cn.roy.springcloud.api2.util;

import org.apache.poi.POIXMLDocumentPart;
import org.apache.poi.POIXMLRelation;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.docx4j.XmlUtils;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.CTColumns;
import org.docx4j.wml.CTDocGrid;
import org.docx4j.wml.*;
import org.openxmlformats.schemas.drawingml.x2006.wordprocessingDrawing.CTAnchor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTAltChunk;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;
import org.springframework.util.CollectionUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBException;
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
public class PoiWordUtil {

    public static void main(String[] args) {
//        List<String> fileList = new ArrayList<>();
//        File file = new File("C:\\Users\\Roy Z Zhou\\Desktop\\word");
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            for (File itemFile : files) {
//                if (itemFile.isFile() && (itemFile.getName().endsWith(".doc") || itemFile.getName().endsWith(".docx"))) {
//                    fileList.add(itemFile.getPath());
//                }
//            }
//        }
//
//        String generateWordPath = "C:\\Users\\Roy Z Zhou\\Desktop\\merge.doc";
        long startTime = System.currentTimeMillis();
//        mergeByPoiAndDocx4j(fileList, generateWordPath);
        addQRCode2WordByPOI();
        System.out.println("耗时：" + ((System.currentTimeMillis() - startTime) / 1000) + "S");
    }

    /**********功能：poi与docx4j实现word合并**********/
    public static boolean mergeByPoiAndDocx4j(List<String> fileList, String mergedFilePath) {
        fileList = preProcessFileList(fileList);
        if (CollectionUtils.isEmpty(fileList)) {
            return false;
        }

        File mergeFile = new File(mergedFilePath);
        File parentFile = mergeFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        if (fileList.size() == 1) {
            try {
                XWPFDocument document = new XWPFDocument(new FileInputStream(new File(fileList.get(0))));
                document.write(new FileOutputStream(mergeFile));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        CTHdrFtr defaultHeaderCTHdrFtr = createDefaultHeader();
        CTHdrFtr defaultFooterCTHdrFtr = createDefaultFooter();
        List<String> mergeList = new ArrayList<>();
        try {
            List<CTSectPr> ctSectPrList = new ArrayList<>();
            for (String filePath : fileList) {
                System.out.println("路径：" + filePath);
                File file = new File(filePath);
                OPCPackage opcPackage = OPCPackage.open(file);
                XWPFDocument document = new XWPFDocument(opcPackage);

                // 设置页码
                CTHdrFtrRef[] headerRef = getHeaderRef(document, defaultHeaderCTHdrFtr);
                CTHdrFtrRef[] footerRef = getFooterRef(document, defaultFooterCTHdrFtr);

                // 拿到本document的分隔符
                CTBody body = document.getDocument().getBody();
                CTSectPr bodySectPr = body.getSectPr();
                // 找到第一个分节符
                CTSectPr firstSec = findFirstCTSectPr(document);
                if (firstSec == null) {
                    firstSec = (CTSectPr) bodySectPr.copy();
                }
                CTSectPr copySec = (CTSectPr) firstSec.copy();
                copySec.setRsidR(firstSec.getRsidR());
                copySec.setRsidRPr(firstSec.getRsidRPr());
                copySec.setRsidSect(firstSec.getRsidSect());
                setSectionTypeAndPgNumberType(copySec);
                ctSectPrList.add(copySec);

                // 删除所有分节符
                removeSection(document);
                // 设置body分节符
                bodySectPr.set(copySec);
                bodySectPr.setHeaderReferenceArray(headerRef);
                bodySectPr.setFooterReferenceArray(footerRef);

                String tempFileName = file.getName().replace(".", "_copy.");
                File tempDir = new File(file.getParent(), File.separator + "temp");
                tempDir.mkdirs();
                File tempFile = new File(tempDir, tempFileName);
                document.write(new FileOutputStream(tempFile));
                mergeList.add(tempFile.getPath());
            }
            mergeByDocx4j(mergeList, mergeFile, ctSectPrList);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (String path : mergeList) {
                File file = new File(path);
                if (file.exists()) {
                    file.delete();
                }
            }
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

    private static CTHdrFtrRef[] getHeaderRef(XWPFDocument document, CTHdrFtr ctHdrFtr) {
        List<XWPFHeader> headerList = document.getHeaderList();
        XWPFHeader header = null;
        if (headerList.size() == 0) {
            header = document.createHeader(HeaderFooterType.DEFAULT);
            header.setHeaderFooter((CTHdrFtr) ctHdrFtr.copy());
        } else {
            header = headerList.get(0);
        }

        // 检测header是否包含其他元素
        List<XWPFParagraph> paragraphs = header.getParagraphs();
        if (paragraphs.size() == 0) {
            // 插入一个极小的段落
            XWPFParagraph paragraph = header.createParagraph();
            CTPPr ctpPr = paragraph.getCTP().addNewPPr();
            CTParaRPr ctParaRPr = ctpPr.addNewRPr();
            CTHpsMeasure ctHpsMeasure = CTHpsMeasure.Factory.newInstance();
            ctHpsMeasure.setVal(BigInteger.valueOf(2));
            ctParaRPr.setSz(ctHpsMeasure);
        }

        // 创建header关联关系
        String relationId = document.getRelationId(header);
        CTHdrFtrRef[] ctHdrFtrRefs = new CTHdrFtrRef[1];
        CTHdrFtrRef ctHdrFtrRef = CTHdrFtrRef.Factory.newInstance();
        ctHdrFtrRef.setType(DEFAULT);
        ctHdrFtrRef.setId(relationId);
        ctHdrFtrRefs[0] = ctHdrFtrRef;

        return ctHdrFtrRefs;
    }

    private static CTHdrFtrRef[] getFooterRef(XWPFDocument document, CTHdrFtr ctHdrFtr) {
        List<XWPFFooter> footerList = document.getFooterList();
        XWPFFooter footer;
        if (footerList.size() == 0) {
            footer = document.createFooter(HeaderFooterType.DEFAULT);
            footer.setHeaderFooter(ctHdrFtr);
        } else {
            footer = footerList.get(0);
            String s = footer._getHdrFtr().xmlText();
            s = s.replace("NUMPAGES", "SECTIONPAGES");
            try {
                CTHdrFtr parse = CTHdrFtr.Factory.parse(s);
                footer.setHeaderFooter(parse);
            } catch (XmlException e) {
                e.printStackTrace();
            }
        }

        // 检测footer是否包含其他元素
        List<XWPFParagraph> paragraphs = footer.getParagraphs();
        if (paragraphs.size() == 0) {
            // 插入一个极小的段落
            XWPFParagraph paragraph = footer.createParagraph();
            CTPPr ctpPr = paragraph.getCTP().addNewPPr();
            CTParaRPr ctParaRPr = ctpPr.addNewRPr();
            CTHpsMeasure ctHpsMeasure = CTHpsMeasure.Factory.newInstance();
            ctHpsMeasure.setVal(BigInteger.valueOf(2));
            ctParaRPr.setSz(ctHpsMeasure);
        }

        // 创建footer关联关系
        String relationId = document.getRelationId(footer);
        CTHdrFtrRef[] ctHdrFtrRefs = new CTHdrFtrRef[1];
        CTHdrFtrRef ctHdrFtrRef = CTHdrFtrRef.Factory.newInstance();
        ctHdrFtrRef.setType(DEFAULT);
        ctHdrFtrRef.setId(relationId);
        ctHdrFtrRefs[0] = ctHdrFtrRef;

        return ctHdrFtrRefs;
    }

    private static CTHdrFtr createDefaultHeader() {
        String s = "<xml-fragment mc:Ignorable=\"w14 w15 w16se wp14\" xmlns:cx=\"http://schemas.microsoft.com/office/drawing/2014/chartex\"\n" +
                "       xmlns:cx1=\"http://schemas.microsoft.com/office/drawing/2015/9/8/chartex\"\n" +
                "       xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"\n" +
                "       xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"\n" +
                "       xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" +
                "       xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n" +
                "       xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n" +
                "       xmlns:w10=\"urn:schemas-microsoft-com:office:word\"\n" +
                "       xmlns:w14=\"http://schemas.microsoft.com/office/word/2010/wordml\"\n" +
                "       xmlns:w15=\"http://schemas.microsoft.com/office/word/2012/wordml\"\n" +
                "       xmlns:w16se=\"http://schemas.microsoft.com/office/word/2015/wordml/symex\"\n" +
                "       xmlns:wne=\"http://schemas.microsoft.com/office/word/2006/wordml\"\n" +
                "       xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\"\n" +
                "       xmlns:wp14=\"http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing\"\n" +
                "       xmlns:wpc=\"http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas\"\n" +
                "       xmlns:wpg=\"http://schemas.microsoft.com/office/word/2010/wordprocessingGroup\"\n" +
                "       xmlns:wpi=\"http://schemas.microsoft.com/office/word/2010/wordprocessingInk\"\n" +
                "       xmlns:wps=\"http://schemas.microsoft.com/office/word/2010/wordprocessingShape\">\n" +
                "    <w:p w:rsidR=\"00D93820\" w:rsidRDefault=\"00D93820\">\n" +
                "        <w:pPr>\n" +
                "            <w:tabs>\n" +
                "                <w:tab w:pos=\"2300\" w:val=\"left\"/>\n" +
                "            </w:tabs>\n" +
                "            <w:ind w:right=\"-36\"/>\n" +
                "            <w:rPr>\n" +
                "                <w:rFonts w:ascii=\"宋体\" w:eastAsia=\"宋体\" w:hAnsi=\"宋体\"/>\n" +
                "                <w:b/>\n" +
                "                <w:kern w:val=\"2\"/>\n" +
                "                <w:sz w:val=\"22\"/>\n" +
                "                <w:szCs w:val=\"22\"/>\n" +
                "            </w:rPr>\n" +
                "        </w:pPr>\n" +
                "    </w:p>\n" +
                "</xml-fragment>";
        CTHdrFtr defaultHeaderCTHdrFtr = null;
        try {
            defaultHeaderCTHdrFtr = CTHdrFtr.Factory.parse(s);
        } catch (XmlException e) {
            e.printStackTrace();
        }

        return defaultHeaderCTHdrFtr;
    }

    private static CTHdrFtr createDefaultFooter() {
        String s1 = "<xml-fragment mc:Ignorable=\"w14 w15 w16se wp14\" xmlns:cx=\"http://schemas.microsoft.com/office/drawing/2014/chartex\"\n" +
                "       xmlns:cx1=\"http://schemas.microsoft.com/office/drawing/2015/9/8/chartex\"\n" +
                "       xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"\n" +
                "       xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"\n" +
                "       xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n" +
                "       xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n" +
                "       xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n" +
                "       xmlns:w10=\"urn:schemas-microsoft-com:office:word\"\n" +
                "       xmlns:w14=\"http://schemas.microsoft.com/office/word/2010/wordml\"\n" +
                "       xmlns:w15=\"http://schemas.microsoft.com/office/word/2012/wordml\"\n" +
                "       xmlns:w16se=\"http://schemas.microsoft.com/office/word/2015/wordml/symex\"\n" +
                "       xmlns:wne=\"http://schemas.microsoft.com/office/word/2006/wordml\"\n" +
                "       xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\"\n" +
                "       xmlns:wp14=\"http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing\"\n" +
                "       xmlns:wpc=\"http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas\"\n" +
                "       xmlns:wpg=\"http://schemas.microsoft.com/office/word/2010/wordprocessingGroup\"\n" +
                "       xmlns:wpi=\"http://schemas.microsoft.com/office/word/2010/wordprocessingInk\"\n" +
                "       xmlns:wps=\"http://schemas.microsoft.com/office/word/2010/wordprocessingShape\">\n" +
                "    <w:sdt>\n" +
                "        <w:sdtPr>\n" +
                "            <w:id w:val=\"1927000404\"/>\n" +
                "            <w:docPartObj>\n" +
                "                <w:docPartGallery w:val=\"Page Numbers (Top of Page)\"/>\n" +
                "                <w:docPartUnique/>\n" +
                "            </w:docPartObj>\n" +
                "        </w:sdtPr>\n" +
                "        <w:sdtEndPr/>\n" +
                "        <w:sdtContent>\n" +
                "            <w:p w:rsidR=\"00D93820\" w:rsidRDefault=\"009662B6\">\n" +
                "                <w:pPr>\n" +
                "                    <w:pStyle w:val=\"Footer\"/>\n" +
                "                    <w:jc w:val=\"center\"/>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                </w:pPr>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:lang w:val=\"zh-CN\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:t xml:space=\"preserve\"> </w:t>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:fldChar w:fldCharType=\"begin\"/>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:instrText>PAGE</w:instrText>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:fldChar w:fldCharType=\"separate\"/>\n" +
                "                </w:r>\n" +
                "                <w:r w:rsidR=\"00E73185\">\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:noProof/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:t>1</w:t>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:fldChar w:fldCharType=\"end\"/>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:lang w:val=\"zh-CN\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:t xml:space=\"preserve\"> / </w:t>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:fldChar w:fldCharType=\"begin\"/>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:instrText>SECTIONPAGES</w:instrText>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:fldChar w:fldCharType=\"separate\"/>\n" +
                "                </w:r>\n" +
                "                <w:r w:rsidR=\"00E73185\">\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:noProof/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:t>5</w:t>\n" +
                "                </w:r>\n" +
                "                <w:r>\n" +
                "                    <w:rPr>\n" +
                "                        <w:b/>\n" +
                "                        <w:bCs/>\n" +
                "                        <w:sz w:val=\"24\"/>\n" +
                "                        <w:szCs w:val=\"24\"/>\n" +
                "                    </w:rPr>\n" +
                "                    <w:fldChar w:fldCharType=\"end\"/>\n" +
                "                </w:r>\n" +
                "            </w:p>\n" +
                "        </w:sdtContent>\n" +
                "    </w:sdt>\n" +
                "</xml-fragment>";
        CTHdrFtr defaultFooterCTHdrFtr = null;
        try {
            defaultFooterCTHdrFtr = CTHdrFtr.Factory.parse(s1);
        } catch (XmlException e) {
            e.printStackTrace();
        }

        return defaultFooterCTHdrFtr;
    }

    private static P createP(CTSectPr originSectPr) {
        SectPr sectPr = new SectPr();

        SectPr.Type type = new SectPr.Type();
        type.setVal("nextPage");
        sectPr.setType(type);
        org.docx4j.wml.CTPageNumber ctPageNumber = new org.docx4j.wml.CTPageNumber();
        ctPageNumber.setStart(BigInteger.valueOf(1));
        sectPr.setPgNumType(ctPageNumber);

        CTPageSz originSectPrPgSz = originSectPr.getPgSz();
        SectPr.PgSz pgSz = new SectPr.PgSz();
        pgSz.setW(originSectPrPgSz.getW());
        pgSz.setH(originSectPrPgSz.getH());
        pgSz.setCode(originSectPrPgSz.getCode());
        sectPr.setPgSz(pgSz);

        // 读取边距
        CTPageMar originSectPrPgMar = originSectPr.getPgMar();
        SectPr.PgMar pgMar = new SectPr.PgMar();
        pgMar.setTop(originSectPrPgMar.getTop());
        pgMar.setRight(originSectPrPgMar.getRight());
        pgMar.setBottom(originSectPrPgMar.getBottom());
        pgMar.setLeft(originSectPrPgMar.getLeft());
        pgMar.setHeader(originSectPrPgMar.getHeader());
        pgMar.setFooter(originSectPrPgMar.getFooter());
        pgMar.setGutter(originSectPrPgMar.getGutter());
        sectPr.setPgMar(pgMar);

        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumns originSectPrCols = originSectPr.getCols();
        CTColumns ctColumns = new CTColumns();
        ctColumns.setSpace(originSectPrCols.getSpace());
        sectPr.setCols(ctColumns);

        BooleanDefaultTrue booleanDefaultTrue = new BooleanDefaultTrue();
        booleanDefaultTrue.setVal(false);
        sectPr.setFormProt(booleanDefaultTrue);

        org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocGrid originSectPrDocGrid = originSectPr.getDocGrid();
        CTDocGrid ctDocGrid = new CTDocGrid();
        ctDocGrid.setCharSpace(originSectPrDocGrid.getCharSpace());
        ctDocGrid.setLinePitch(originSectPrDocGrid.getLinePitch());
        sectPr.setDocGrid(ctDocGrid);

        P p = new P();
        String s1 = originSectPr.xmlText();
        s1 = s1.replace("xml-fragment", "w:sectPr");
        try {
            SectPr sec = (SectPr) XmlUtils.unmarshalString(s1);
            p.setRsidR(sec.getRsidR());
            p.setRsidRDefault(sec.getRsidR());
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        PPr pPr = new PPr();
        pPr.setSectPr(sectPr);
        p.setPPr(pPr);

        return p;
    }

    private static boolean mergeByDocx4j(List<String> fileList, File mergeFile, List<CTSectPr> ctSectPrList) {
        try {
            WordprocessingMLPackage wordprocessingMLPackage = WordprocessingMLPackage.createPackage();
            MainDocumentPart mainDocumentPart = wordprocessingMLPackage.getMainDocumentPart();
            // 清除默认的段落样式（阻止header影响格式）
            StyleDefinitionsPart styleDefinitionsPart = mainDocumentPart.getStyleDefinitionsPart();
            Styles contents = styleDefinitionsPart.getContents();
            DocDefaults docDefaults = contents.getDocDefaults();
            docDefaults.setPPrDefault(new DocDefaults.PPrDefault());
            for (int i = 0; i < fileList.size(); i++) {
                File docFile = new File(fileList.get(i));
                PartName partName = new PartName("/word/part/" + docFile.getName());
                AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(partName);
                afiPart.setAltChunkType(AltChunkType.WordprocessingML);
                afiPart.setBinaryData(new FileInputStream(docFile));
                Relationship altChunkRel = mainDocumentPart.addTargetPart(afiPart);
                altChunkRel.setTargetMode("Internal");
                org.docx4j.wml.CTAltChunk ctAltChunk = Context.getWmlObjectFactory().createCTAltChunk();
                org.docx4j.wml.CTAltChunkPr ctAltChunkPr = new org.docx4j.wml.CTAltChunkPr();
                BooleanDefaultTrue booleanDefaultTrue = new BooleanDefaultTrue();
                booleanDefaultTrue.setVal(true);
                ctAltChunkPr.setMatchSrc(booleanDefaultTrue);
                ctAltChunk.setAltChunkPr(ctAltChunkPr);
                ctAltChunk.setId(altChunkRel.getId());
                mainDocumentPart.addObject(ctAltChunk);

                // 插入下一节
                P p = createP(ctSectPrList.get(i));
                mainDocumentPart.addObject(p);
            }
            // 去除尾页空白页
            SectPr sectPr = mainDocumentPart.getContents().getBody().getSectPr();
            SectPr.PgSz pgSz = sectPr.getPgSz();
            pgSz.setH(BigInteger.ZERO);
            pgSz.setW(BigInteger.ZERO);

            wordprocessingMLPackage.save(mergeFile);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Docx4JException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**********功能：纯poi操作word合并**********/
    public static boolean mergeByPoi(List<String> fileList, String mergedFilePath) {
        fileList = preProcessFileList(fileList);
        if (CollectionUtils.isEmpty(fileList)) {
            return false;
        }

        try {
            File file = new File(fileList.get(0));
            OPCPackage opcPackage = OPCPackage.open(file);
            XWPFDocument mainDocument = new XWPFDocument(opcPackage);
            if (fileList.size() > 1) {
                List<XWPFDocument> documentList = new ArrayList<>();
                // 分节符列表
                List<CTSectPr> ctSectPrList = new ArrayList<>();
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
                        XWPFRelation relation = XWPFRelation.FOOTER;
                        int id = mainDocument.getRelationParts().size() + 1;
                        XWPFFooter xwpfFooter = (XWPFFooter) mainDocument.createRelationship(relation,
                                XWPFFactory.getInstance(), id);
                        xwpfFooter.setHeaderFooter(parse);
                        relationId = mainDocument.getRelationId(xwpfFooter);

                        System.out.println("********************分割线开始********************");
//                        PackagePartName partName = PackagingURIHelper.createPartName(String.format("/word/part%d.docx", i));
//                        PackagePart part = opcPackage.createPart(partName, XWPFRelation.DOCUMENT.getContentType());
//                        part.load(new FileInputStream(new File(fileList.get(i))));
//                        int id = mainDocument.getRelationParts().size() + 1;
//                        POIXMLDocumentPart poixmlDocumentPart = new POIXMLDocumentPart(part);
//                        POIXMLDocumentPart relationship = mainDocument.createRelationship(ChunkRelation.AF_CHUNK,
//                                XWPFFactory.getInstance(), id);
//                        POIXMLDocumentPart.RelationPart relationPart = mainDocument.addRelation("rId" + id,
//                                ChunkRelation.AF_CHUNK, poixmlDocumentPart);
//                        PackageRelationship relationship1 = relationPart.getRelationship();
//                        CTAltChunk ctAltChunk = mainDocument.getDocument().getBody().addNewAltChunk();
//                        ctAltChunk.setId(relationship1.getId());
                        System.out.println("********************分割线开始********************");
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

                removeSection(mainDocument);
                removeBodySection(mainDocument);
                addPageBreak(mainDocument, ctSectPrList.get(0));
                for (int i = 1; i < documentList.size(); i++) {
                    XWPFDocument document = documentList.get(i);
                    removeSection(document);
                    if (i == documentList.size() - 1) {
                        document.removeBodyElement(document.getBodyElements().size() - 2);
                        CTSectPr sectPr = document.getDocument().getBody().getSectPr();
                        setSectionTypeAndPgNumberType(sectPr);
                    } else {
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

            XWPFDocument D = new XWPFDocument(new FileInputStream(mergeFile));
            List<POIXMLDocumentPart> relations = mainDocument.getRelations();

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

    private static void processDocumentRelationShip(XWPFDocument mainDocument, XWPFDocument document) {
        // 样式
//        XWPFStyles mainStyles = mainDocument.getStyles();
//        XWPFStyles itemStyles = document.getStyles();
//        CTStyles itemCtStyles = null;
//        try {
//            itemCtStyles = document.getStyle();
//            List<CTStyle> itemStyleList = itemCtStyles.getStyleList();
//            for (CTStyle ctStyle : itemStyleList) {
//                String styleId = ctStyle.getStyleId();
//                XWPFStyle style = itemStyles.getStyle(styleId);
//                mainStyles.addStyle(style);
//            }
//        } catch (XmlException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        int id = mainDocument.getRelationParts().size() + 1;
        PackagePart packagePart = document.getPackagePart();
        POIXMLDocumentPart poixmlDocumentPart = new POIXMLDocumentPart(mainDocument, packagePart);
        POIXMLDocumentPart relationship = mainDocument.createRelationship(XWPFRelation.MACRO_DOCUMENT,
                XWPFFactory.getInstance(), id);
        POIXMLDocumentPart.RelationPart relationPart = mainDocument.addRelation("rId" + id,
                XWPFRelation.DOCUMENT, poixmlDocumentPart);
        CTAltChunk ctAltChunk = mainDocument.getDocument().getBody().addNewAltChunk();
        ctAltChunk.setId(relationPart.getRelationship().getId());

//        List<POIXMLDocumentPart.RelationPart> relationParts = document.getRelationParts();
//        for (POIXMLDocumentPart.RelationPart part : relationParts) {
//            PackageRelationship relationship = part.getRelationship();
//            POIXMLDocumentPart documentPart = part.getDocumentPart();
//        }


//        List<POIXMLDocumentPart.RelationPart> relationPartList = document.getRelationParts();
//        for (POIXMLDocumentPart.RelationPart relationPart : relationPartList) {
//            PackageRelationship relationship = relationPart.getRelationship();
//            String relationshipType = relationship.getRelationshipType();
//            PackagePart source = relationship.getSource();
//
//            POIXMLDocumentPart poixmlDocumentPart = relationPart.getDocumentPart();
//
//            relationPart.getDocumentPart();
//            if (relationshipType.equals(Namespaces.FONT_TABLE)) {
//                POIXMLDocumentPart documentPart = relationPart.getDocumentPart();
//                String rId = "rId" + (mainDocument.getRelationParts().size() + 1);
//                XWPFRelation instance = XWPFRelation.getInstance(relationshipType);
//                mainDocument.addRelation(rId, instance, documentPart);
//            }
//        }
    }

    private static CTSectPr findFirstCTSectPr(XWPFDocument document) {
        CTP[] pArray = document.getDocument().getBody().getPArray();
        CTSectPr sectPr = null;
        for (CTP ctp : pArray) {
            String ctpStr = ctp.xmlText();
            if (ctpStr.contains("w:sectPr")) {
                CTPPr pPr = ctp.getPPr();
                sectPr = pPr.getSectPr();
                break;
            }
        }
        return sectPr;
    }

    private static void setSectionTypeAndPgNumberType(CTSectPr sectPr) {
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

    private static void removeSection(XWPFDocument document) {
        CTP[] pArray = document.getDocument().getBody().getPArray();
        int index = 0;
        List<Integer> remove = new ArrayList<>();
        for (CTP ctp : pArray) {
            String ctpStr = ctp.xmlText();
            if (ctpStr.contains("w:sectPr")) {
                remove.add(index);
            }
            index++;
        }
        List<Integer> list = new ArrayList<>();
        for (Integer i : remove) {
            XWPFParagraph paragraphArray = document.getParagraphArray(i);
            document.getParagraph(paragraphArray.getCTP());
            int paragraphPos = document.getPosOfParagraph(paragraphArray);
            list.add(paragraphPos);
        }
        Collections.reverse(list);
        for (Integer i : list) {
            document.removeBodyElement(i);
        }
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

    /**********功能：添加二维码**********/
    private static void addQRCode2WordByPOI() {
        String docFilePath = "C:\\Users\\Roy Z Zhou\\Desktop\\word\\test.doc";
        String imgFilePath = "C:\\Users\\Roy Z Zhou\\Pictures\\1.jpeg";
        File file = new File(docFilePath);
        XWPFDocument document = null;
        try {
            document = new XWPFDocument(new FileInputStream(file));
            CTBody body = document.getDocument().getBody();
            CTP[] pArray = body.getPArray();
            CTP ctp;
            if (pArray.length > 0) {
                ctp = pArray[0];
            } else {
                ctp = document.createParagraph().getCTP();
            }

            String relationId = document.addPictureData(new FileInputStream(new File(imgFilePath)),
                    Document.PICTURE_TYPE_JPEG);
            XWPFPictureData picData = (XWPFPictureData) document.getRelationById(relationId);
            String fileName = picData.getFileName();
            int picId = new Random().nextInt(10000);
            HashMap<String, String> map = new HashMap<>();
            map.put("rEmbedId", relationId);
            map.put("docName", "Picture " + picId);
            map.put("picId", Integer.toString(picId));
            map.put("picName", fileName);
            String anchorStr = replace(map);

            CTDrawing drawing = ctp.addNewR().addNewDrawing();
            CTAnchor anchor = CTAnchor.Factory.parse(anchorStr);
            drawing.set(anchor);

            File mergeFile = new File(docFilePath.replace(".", "_1."));
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
        String template =
                "<wp:anchor xmlns:a=\"http://schemas.openxmlformats.org/drawingml/2006/main\"\n" +
                "           xmlns:b=\"http://schemas.openxmlformats.org/officeDocument/2006/bibliography\"\n" +
                "           xmlns:c=\"http://schemas.openxmlformats.org/drawingml/2006/chart\"\n" +
                "           xmlns:c14=\"http://schemas.microsoft.com/office/drawing/2007/8/2/chart\"\n" +
                "           xmlns:cdr=\"http://schemas.openxmlformats.org/drawingml/2006/chartDrawing\"\n" +
                "           xmlns:comp=\"http://schemas.openxmlformats.org/drawingml/2006/compatibility\"\n" +
                "           xmlns:cppr=\"http://schemas.microsoft.com/office/2006/coverPageProps\"\n" +
                "           xmlns:dgm=\"http://schemas.openxmlformats.org/drawingml/2006/diagram\"\n" +
                "           xmlns:dsp=\"http://schemas.microsoft.com/office/drawing/2008/diagram\"\n" +
                "           xmlns:lc=\"http://schemas.openxmlformats.org/drawingml/2006/lockedCanvas\"\n" +
                "           xmlns:m=\"http://schemas.openxmlformats.org/officeDocument/2006/math\"\n" +
                "           xmlns:mc=\"http://schemas.openxmlformats.org/markup-compatibility/2006\"\n" +
                "           xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:oda=\"http://opendope.org/answers\"\n" +
                "           xmlns:odc=\"http://opendope.org/conditions\" xmlns:odgm=\"http://opendope.org/SmartArt/DataHierarchy\"\n" +
                "           xmlns:odi=\"http://opendope.org/components\" xmlns:odq=\"http://opendope.org/questions\"\n" +
                "           xmlns:odx=\"http://opendope.org/xpaths\" xmlns:pic=\"http://schemas.openxmlformats.org/drawingml/2006/picture\"\n" +
                "           xmlns:pvml=\"urn:schemas-microsoft-com:office:powerpoint\"\n" +
                "           xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\"\n" +
                "           xmlns:sl=\"http://schemas.openxmlformats.org/schemaLibrary/2006/main\" xmlns:v=\"urn:schemas-microsoft-com:vml\"\n" +
                "           xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\"\n" +
                "           xmlns:w10=\"urn:schemas-microsoft-com:office:word\"\n" +
                "           xmlns:w14=\"http://schemas.microsoft.com/office/word/2010/wordml\"\n" +
                "           xmlns:w15=\"http://schemas.microsoft.com/office/word/2012/wordml\"\n" +
                "           xmlns:w16cid=\"http://schemas.microsoft.com/office/word/2016/wordml/cid\"\n" +
                "           xmlns:w16se=\"http://schemas.microsoft.com/office/word/2015/wordml/symex\"\n" +
                "           xmlns:we=\"http://schemas.microsoft.com/office/webextensions/webextension/2010/11\"\n" +
                "           xmlns:wetp=\"http://schemas.microsoft.com/office/webextensions/taskpanes/2010/11\"\n" +
                "           xmlns:wne=\"http://schemas.microsoft.com/office/word/2006/wordml\"\n" +
                "           xmlns:wp=\"http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing\"\n" +
                "           xmlns:wp14=\"http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing\"\n" +
                "           xmlns:wps=\"http://schemas.microsoft.com/office/word/2010/wordprocessingShape\"\n" +
                "           xmlns:xdr=\"http://schemas.openxmlformats.org/drawingml/2006/spreadsheetDrawing\"\n" +
                "           xmlns:xvml=\"urn:schemas-microsoft-com:office:excel\"\n"+
                "           distT=\"0\" distB=\"0\" distL=\"114300\" distR=\"114300\" simplePos=\"0\"\n" +
                "           relativeHeight=\"251658240\" behindDoc=\"0\" locked=\"0\" layoutInCell=\"0\"\n" +
                "           allowOverlap=\"1\">\n" +
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
                "                            <a:blip r:embed=\"${rEmbedId}\" />\n" +
                "                            <a:stretch>\n" +
                "                                <a:fillRect/>\n" +
                "                            </a:stretch>\n" +
                "                        </pic:blipFill>\n" +
                "                        <pic:spPr>\n" +
                "                            <a:xfrm>\n" +
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
