package com.confeitaria.infra.doclet;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Set;

public class ListaDoclet implements Doclet {

    @Override
    public void init(Locale locale, Reporter reporter) {}

    @Override
    public String getName() { return "GeradorMarkdownConfeitaria"; }

    @Override
    public SourceVersion getSupportedSourceVersion() { return SourceVersion.latest(); }

    @Override
    public boolean run(DocletEnvironment environment) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("docs/documentation.md"))) {
            writer.println("# 🎂 Documentação do Sistema de Confeitaria");
            writer.println("Documentação técnica gerada automaticamente via **Custom Doclet**.\n");

            for (Element pkg : environment.getIncludedElements()) {
                if (pkg.getKind() == ElementKind.PACKAGE) {
                    writer.println("## 📦 Pacote: `" + pkg.getSimpleName() + "`");
                    
                    for (Element clazz : pkg.getEnclosedElements()) {
                        if (clazz instanceof TypeElement typeElement) {
                            writer.println("### 📄 Classe: `" + clazz.getSimpleName() + "`");
                            
                            // Pegar comentário JavaDoc se existir
                            String doc = environment.getElementUtils().getDocComment(clazz);
                            if (doc != null) writer.println("> " + doc.trim().replace("\n", " "));

                            writer.println("| Elemento | Nome |");
                            writer.println("| :--- | :--- |");

                            for (Element member : clazz.getEnclosedElements()) {
                                if (member.getKind() == ElementKind.FIELD) {
                                    writer.println("| 🔹 Atributo | `" + member.getSimpleName() + "` |");
                                } else if (member.getKind() == ElementKind.METHOD) {
                                    writer.println("| ⚙️ Método | `" + member.getSimpleName() + "()` |");
                                }
                            }
                            writer.println("\n---");
                        }
                    }
                }
            }
            System.out.println("✅ Sucesso! O arquivo docs/documentation.md foi gerado.");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Set<? extends Option> getSupportedOptions() { return Set.of(); }
}