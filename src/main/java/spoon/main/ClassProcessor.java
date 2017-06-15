package spoon.main;

import spoon.reflect.factory.AnnotationFactory;
import spoon.processing.AbstractProcessor;
import codesmells.annotations.Blob;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import java.util.List;
import java.util.Set;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;
import java.util.HashMap;
import java.util.Map;
import codesmells.annotations.LM;
import org.apache.log4j.Level;

public class ClassProcessor extends AbstractProcessor<CtClass> {
    @Override
    public void process(CtClass element) {
        System.out.println(("Class:" + (element.getSimpleName())));
        blobDetection(element);
    }

    @LM
    private void blobDetection(CtClass element) {
        boolean added = false;
        Class<codesmells.annotations.Blob> annotationType = Blob.class;
        AnnotationFactory factory = new AnnotationFactory(element.getFactory());
        CtAnnotation<?> annotation = factory.annotate(element, annotationType);
        codesmells.annotations.Blob blob = element.getAnnotation(Blob.class);
        Map<String, Object> values = new HashMap<>();
        if ((element.getMethods().size()) > (blob.limitMethods())) {
            getFactory().getEnvironment().report(this, Level.WARN, element, "Blob code smell");
            values.put("limitMethods", blob.limitMethods());
            values.put("currentMethods", element.getMethods().size());
            added = true;
        }
        if ((element.getFields().size()) > (blob.limitAttributes())) {
            getFactory().getEnvironment().report(this, Level.WARN, element, "Blob code smell");
            values.put("limitAttributes", blob.limitAttributes());
            values.put("currentAttributes", element.getFields().size());
            added = true;
        }
        int lcom = computeLCOM(element);
        System.out.println(("Method lack cohesion:" + lcom));
        if (lcom > (blob.limitLackOfCohesionMethods())) {
            getFactory().getEnvironment().report(this, Level.WARN, element, "Blob code smell");
            values.put("limitLackOfCohesionMethods", blob.limitLackOfCohesionMethods());
            values.put("currentLackOfCohesionMethods", lcom);
            added = true;
        }
        if (!added)
            element.removeAnnotation(annotation);
        else
            annotation.setElementValues(values);
        
    }

    @LM
    public int computeLCOM(CtClass element) {
        Set<CtMethod> setmethods = element.getAllMethods();
        CtMethod[] methods = new CtMethod[setmethods.size()];
        int index = 0;
        for (CtMethod method : setmethods) {
            methods[index] = method;
            index++;
        }
        int methodCount = methods.length;
        int haveFieldInCommon = 0;
        int noFieldInCommon = 0;
        List<CtVariable> usedVariables;
        for (int i = 0; i < methodCount; i++) {
            for (int j = i + 1; j < methodCount; j++) {
                if ((methods[i]) == null)
                    continue;
                
                if ((methods[i].getBody()) == null)
                    continue;
                
                usedVariables = methods[i].getBody().getElements(new TypeFilter(CtVariable.class));
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

    private boolean haveCommonFields(List<CtVariable> usedVariables, CtMethod element) {
        if (element == null)
            return false;
        
        if ((element.getBody()) == null)
            return false;
        
        List<CtVariable> otherVariables = element.getBody().getElements(new TypeFilter(CtVariable.class));
        for (CtVariable paprikaVariable : usedVariables) {
            if (otherVariables.contains(paprikaVariable))
                return true;
            
        }
        return false;
    }
}

