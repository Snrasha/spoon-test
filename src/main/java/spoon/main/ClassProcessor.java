package spoon.main;


@codesmells.annotations.Blob(currentLackOfCohesionMethods = 489, limitLackOfCohesionMethods = 40)
public class ClassProcessor extends spoon.processing.AbstractProcessor<spoon.reflect.declaration.CtClass> {
    @java.lang.Override
    public void process(spoon.reflect.declaration.CtClass element) {
        java.lang.System.out.println(("Class:" + (element.getSimpleName())));
        blobDetection(element);
    }

    private void blobDetection(spoon.reflect.declaration.CtClass element) {
        boolean added = false;
        java.lang.Class<codesmells.annotations.Blob> annotationType = codesmells.annotations.Blob.class;
        spoon.reflect.factory.AnnotationFactory factory = new spoon.reflect.factory.AnnotationFactory(element.getFactory());
        spoon.reflect.declaration.CtAnnotation<?> annotation = factory.annotate(element, annotationType);
        codesmells.annotations.Blob blob = element.getAnnotation(codesmells.annotations.Blob.class);
        java.util.Map<java.lang.String, java.lang.Object> values = new java.util.HashMap<>();
        if ((element.getMethods().size()) > (blob.limitMethods())) {
            getFactory().getEnvironment().report(this, org.apache.log4j.Level.WARN, element, "Blob code smell");
            values.put("limitMethods", blob.limitMethods());
            values.put("currentMethods", element.getMethods().size());
            added = true;
        }
        if ((element.getFields().size()) > (blob.limitAttributes())) {
            getFactory().getEnvironment().report(this, org.apache.log4j.Level.WARN, element, "Blob code smell");
            values.put("limitAttributes", blob.limitAttributes());
            values.put("currentAttributes", element.getFields().size());
            added = true;
        }
        int lcom = computeLCOM(element);
        java.lang.System.out.println(("Method lack cohesion:" + lcom));
        if (lcom > (blob.limitLackOfCohesionMethods())) {
            getFactory().getEnvironment().report(this, org.apache.log4j.Level.WARN, element, "Blob code smell");
            values.put("limitLackOfCohesionMethods", blob.limitLackOfCohesionMethods());
            values.put("currentLackOfCohesionMethods", lcom);
            added = true;
        }
        if (!added)
            element.removeAnnotation(annotation);
        else
            annotation.setElementValues(values);
        
    }

    public int computeLCOM(spoon.reflect.declaration.CtClass element) {
        java.util.Set<spoon.reflect.declaration.CtMethod> setmethods = element.getAllMethods();
        spoon.reflect.declaration.CtMethod[] methods = new spoon.reflect.declaration.CtMethod[setmethods.size()];
        int index = 0;
        for (spoon.reflect.declaration.CtMethod method : setmethods) {
            methods[index] = method;
            index++;
        }
        int methodCount = methods.length;
        int haveFieldInCommon = 0;
        int noFieldInCommon = 0;
        java.util.List<spoon.reflect.declaration.CtVariable> usedVariables;
        for (int i = 0; i < methodCount; i++) {
            for (int j = i + 1; j < methodCount; j++) {
                if ((methods[i]) == null)
                    continue;
                
                if ((methods[i].getBody()) == null)
                    continue;
                
                usedVariables = methods[i].getBody().getElements(new spoon.reflect.visitor.filter.TypeFilter(spoon.reflect.declaration.CtVariable.class));
                if (this.haveCommonFields(usedVariables, methods[j])) {
                    haveFieldInCommon++;
                }else {
                    noFieldInCommon++;
                }
            }
        }
        int LCOM = noFieldInCommon - haveFieldInCommon;
        return LCOM > 0 ? LCOM : 0;
    }

    private boolean haveCommonFields(java.util.List<spoon.reflect.declaration.CtVariable> usedVariables, spoon.reflect.declaration.CtMethod element) {
        if (element == null)
            return false;
        
        if ((element.getBody()) == null)
            return false;
        
        java.util.List<spoon.reflect.declaration.CtVariable> otherVariables = element.getBody().getElements(new spoon.reflect.visitor.filter.TypeFilter(spoon.reflect.declaration.CtVariable.class));
        for (spoon.reflect.declaration.CtVariable paprikaVariable : usedVariables) {
            if (otherVariables.contains(paprikaVariable))
                return true;
            
        }
        return false;
    }
}

