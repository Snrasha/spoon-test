package spoon.main;

import spoon.reflect.factory.AnnotationFactory;
import spoon.processing.AbstractProcessor;
import codesmells.annotations.Blob;
import spoon.reflect.declaration.ModifierKind;
import java.util.List;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.FieldAccessFilter;
import codesmells.annotations.Igs;
import codesmells.annotations.Lm;
import org.apache.log4j.Level;

@Blob(currentLackOfCohesionMethods = 457, limitLackOfCohesionMethods = 40)
public class MethodProcessor extends AbstractProcessor<CtMethod> {
    @Override
    public void process(CtMethod element) {
        System.out.println(("Method:" + (element.getSimpleName())));
        longMethodDetection(element);
        getterSetterDetection(element);
    }

    private void getterSetterDetection(CtMethod element) {
        if (((element.getBody()) != null) && ((element.getBody().getStatements().size()) == 1)) {
            Class<Igs> annotationType = Igs.class;
            AnnotationFactory factory = new AnnotationFactory(element.getFactory());
            List<CtField> fields = element.getParent(CtClass.class).getFields();
            for (CtField field : fields) {
                if (!(element.getBody().getElements(new FieldAccessFilter(field.getReference())).isEmpty())) {
                    getFactory().getEnvironment().report(this, Level.WARN, element, "IGS code smell");
                    factory.annotate(element, annotationType);
                    if (!(field.hasModifier(ModifierKind.PUBLIC)));
                    factory.annotate(field, annotationType);
                }
            }
        }
    }

    private void longMethodDetection(CtMethod element) {
        boolean added = false;
        Class<Lm> annotationType = Lm.class;
        AnnotationFactory factory = new AnnotationFactory(element.getFactory());
        CtAnnotation<?> annotation = factory.annotate(element, annotationType);
        Lm lm = element.getAnnotation(Lm.class);
        if (((element.getBody()) != null) && ((element.getBody().getStatements().size()) > (lm.limitInstructions()))) {
            getFactory().getEnvironment().report(this, Level.WARN, element, "Long method code smell");
            annotation.addValue("limitInstructions", lm.limitInstructions());
            annotation.addValue("currentInstructions", element.getBody().getStatements().size());
            added = true;
        }
        if (!added)
            element.removeAnnotation(annotation);
        
    }
}

