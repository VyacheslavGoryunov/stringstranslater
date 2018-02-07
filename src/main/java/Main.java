import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

import javax.swing.*;

/**
 * Created by VyacheslavGoryunov on 07.02.2018.
 * stringstranslater © 2018
 */
public class Main {
    public static void main(String[] args) {
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        JFrame frame = new JFrame("Strings Translator");
        String defaultLang = JOptionPane.showInputDialog(frame, "Default language code");
        String targetLang = JOptionPane.showInputDialog(frame, "Target language code");

        try {
            File fXmlFile = new File(args[0]);

            if(!(new File("modified/")).exists()) {
                new File("modified/").mkdir();
            }

            File targetXml = new File("modified/" + fXmlFile.getName());

            if(!targetXml.exists())
                targetXml.createNewFile();

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element: " + doc.getDocumentElement().getNodeName());

            NodeList list = doc.getElementsByTagName("string");

            for (int i = 0; i < list.getLength(); i++) {

                Element element = (Element) list.item(i);

                String information = "";

                information += (i + 1) + " of " + list.getLength();
                information += "\nCurrent Element: " + element.getAttribute("name");
                information += "\nCurrent text: " + element.getTextContent();

                Translation translation =
                        translate.translate(
                                element.getTextContent(),
                                Translate.TranslateOption.sourceLanguage(defaultLang),
                                Translate.TranslateOption.targetLanguage(targetLang));

                information += "\nTranslation: " + translation.getTranslatedText();
                information += "\n\nSave?";

                String input = JOptionPane.showInputDialog(frame, information, translation.getTranslatedText());

                if(input != null) {
                    element.setTextContent(input);
                    save(doc, targetXml);
                }
            }
        } catch (Exception ignored) {
            System.out.println(ignored);
        }
    }

    private static void save(Document doc, File target) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        Result output = new StreamResult(target);
        Source input = new DOMSource(doc);

        transformer.transform(input, output);

        System.out.println("Saved");
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
