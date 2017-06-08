package spoon.main;


@codesmells.annotations.Blob(currentLackOfCohesionMethods = 427, limitLackOfCohesionMethods = 40)
public class InterfaceProcessor extends spoon.processing.AbstractProcessor<spoon.reflect.declaration.CtInterface> {
    @java.lang.Override
    public void process(spoon.reflect.declaration.CtInterface element) {
        java.lang.System.out.println(("Interface:" + (element.getSimpleName())));
        SakDetection(element);
    }

    private void SakDetection(spoon.reflect.declaration.CtInterface element) {
        if (element.isInterface()) {
            boolean added = false;
            java.lang.Class<codesmells.annotations.Sak> annotationType = codesmells.annotations.Sak.class;
            spoon.reflect.factory.AnnotationFactory factory = new spoon.reflect.factory.AnnotationFactory(element.getFactory());
            spoon.reflect.declaration.CtAnnotation<?> annotation = factory.annotate(element, annotationType);
            codesmells.annotations.Sak sak = element.getAnnotation(codesmells.annotations.Sak.class);
            if ((element.getMethods().size()) > (sak.limitMethods())) {
                getFactory().getEnvironment().report(this, org.apache.log4j.Level.WARN, element, "Sak code smell");
                java.util.Map<java.lang.String, java.lang.Object> values = new java.util.HashMap<>();
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

