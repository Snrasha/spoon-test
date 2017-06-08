package spoon.main;


@codesmells.annotations.Blob(currentLackOfCohesionMethods = 457, limitLackOfCohesionMethods = 40)
public class MethodProcessor extends spoon.processing.AbstractProcessor<spoon.reflect.declaration.CtMethod> {
    @java.lang.Override
    public void process(spoon.reflect.declaration.CtMethod element) {
        java.lang.System.out.println(("Method:" + (element.getSimpleName())));
        longMethodDetection(element);
        getterSetterDetection(element);
    }

    private void getterSetterDetection(spoon.reflect.declaration.CtMethod element) {
        if (((element.getBody()) != null) && ((element.getBody().getStatements().size()) == 1)) {
            java.lang.Class<codesmells.annotations.Igs> annotationType = codesmells.annotations.Igs.class;
            spoon.reflect.factory.AnnotationFactory factory = new spoon.reflect.factory.AnnotationFactory(element.getFactory());
            java.util.List<spoon.reflect.declaration.CtField> fields = element.getParent(spoon.reflect.declaration.CtClass.class).getFields();
            for (spoon.reflect.declaration.CtField field : fields) {
                if (!(element.getBody().getElements(new spoon.reflect.visitor.filter.FieldAccessFilter(field.getReference())).isEmpty())) {
                    getFactory().getEnvironment().report(this, org.apache.log4j.Level.WARN, element, "IGS code smell");
                    factory.annotate(element, annotationType);
                    if (!(field.hasModifier(spoon.reflect.declaration.ModifierKind.PUBLIC)));
                    factory.annotate(field, annotationType);
                }
            }
        }
    }

    private void longMethodDetection(spoon.reflect.declaration.CtMethod element) {
        boolean added = false;
        java.lang.Class<codesmells.annotations.Lm> annotationType = codesmells.annotations.Lm.class;
        spoon.reflect.factory.AnnotationFactory factory = new spoon.reflect.factory.AnnotationFactory(element.getFactory());
        spoon.reflect.declaration.CtAnnotation<?> annotation = factory.annotate(element, annotationType);
        codesmells.annotations.Lm lm = element.getAnnotation(codesmells.annotations.Lm.class);
        if (((element.getBody()) != null) && ((element.getBody().getStatements().size()) > (lm.limitInstructions()))) {
            getFactory().getEnvironment().report(this, org.apache.log4j.Level.WARN, element, "Long method code smell");
            annotation.addValue("limitInstructions", lm.limitInstructions());
            annotation.addValue("currentInstructions", element.getBody().getStatements().size());
            added = true;
        }
        if (!added)
            element.removeAnnotation(annotation);
        
    }
}

