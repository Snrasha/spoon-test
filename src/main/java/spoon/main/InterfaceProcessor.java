package spoon.main;

import spoon.processing.AbstractProcessor;
import spoon.reflect.factory.AnnotationFactory;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtInterface;
import java.util.HashMap;
import org.apache.log4j.Level;
import java.util.Map;
import codesmells.annotations.Sak;

public class InterfaceProcessor extends AbstractProcessor<CtInterface> {
    @Override
    public void process(CtInterface element) {
        System.out.println(("Interface:" + (element.getSimpleName())));
        SakDetection(element);
    }

    private void SakDetection(CtInterface element) {
        if (element.isInterface()) {
            boolean added = false;
            Class<codesmells.annotations.Sak> annotationType = Sak.class;
            AnnotationFactory factory = new AnnotationFactory(element.getFactory());
            CtAnnotation<?> annotation = factory.annotate(element, annotationType);
            codesmells.annotations.Sak sak = element.getAnnotation(Sak.class);
            if ((element.getMethods().size()) > (sak.limitMethods())) {
                getFactory().getEnvironment().report(this, Level.WARN, element, "Sak code smell");
                Map<String, Object> values = new HashMap<>();
                values.put("limitMethods", sak.limitMethods());
                values.put("currentMethods", element.getMethods().size());
                annotation.setElementValues(values);
                added = true;
            }
            if (!added)
                element.removeAnnotation(annotation);
            
        }
    }
}

