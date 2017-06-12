package spoon.main;

import spoon.processing.AbstractProcessor;
import spoon.reflect.factory.AnnotationFactory;
import codesmells.annotations.Blob;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtInterface;
import codesmells.annotations.Sak;
import java.util.HashMap;
import org.apache.log4j.Level;
import java.util.Map;

@Blob(currentLackOfCohesionMethods = 427, limitLackOfCohesionMethods = 40)
public class InterfaceProcessor extends AbstractProcessor<CtInterface> {
    @Override
    public void process(CtInterface element) {
        System.out.println(("Interface:" + (element.getSimpleName())));
        SakDetection(element);
    }

    private void SakDetection(CtInterface element) {
        if (element.isInterface()) {
            boolean added = false;
            Class<Sak> annotationType = Sak.class;
            AnnotationFactory factory = new AnnotationFactory(element.getFactory());
            CtAnnotation<?> annotation = factory.annotate(element, annotationType);
            Sak sak = element.getAnnotation(Sak.class);
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

