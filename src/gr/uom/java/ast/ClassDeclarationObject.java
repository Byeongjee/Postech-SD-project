package gr.uom.java.ast;

import gr.uom.java.ast.decomposition.CatchClauseObject;
import gr.uom.java.ast.decomposition.TryStatementObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ITypeRoot;

public abstract class ClassDeclarationObject {
	protected String name;
	protected List<MethodObject> methodList;
	protected List<FieldObject> fieldList;
	protected List<CommentObject> commentList;
	
	public ClassDeclarationObject() {
		this.methodList = new ArrayList<MethodObject>();
		this.fieldList = new ArrayList<FieldObject>();
		this.commentList = new ArrayList<CommentObject>();
	}

	public abstract ITypeRoot getITypeRoot();
	public abstract ClassObject getClassObject();
	public abstract IFile getIFile();
	public abstract TypeObject getSuperclass();

	public boolean addMethod(MethodObject method) {
		return methodList.add(method);
	}

	public boolean addField(FieldObject f) {
		return fieldList.add(f);
	}

	public boolean addComment(CommentObject comment) {
		return commentList.add(comment);
	}

	public boolean addComments(List<CommentObject> comments) {
		return commentList.addAll(comments);
	}

	public List<MethodObject> getMethodList() {
		return methodList;
	}

	public ListIterator<MethodObject> getMethodIterator() {
		return methodList.listIterator();
	}

	public ListIterator<FieldObject> getFieldIterator() {
		return fieldList.listIterator();
	}

	public ListIterator<CommentObject> getCommentIterator() {
		return commentList.listIterator();
	}

	public int getNumberOfMethods() {
		return methodList.size();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean containsMethodWithTestAnnotation() {
    	for(MethodObject method : methodList) {
    		if(method.hasTestAnnotation())
    			return true;
    	}
    	return false;
    }

    public MethodObject getMethod(MethodInvocationObject mio) {
        ListIterator<MethodObject> mi = getMethodIterator();
        while(mi.hasNext()) {
            MethodObject mo = mi.next();
            if(mo.equals(mio))
                return mo;
        }
        return null;
    }

    public MethodObject getMethod(SuperMethodInvocationObject smio) {
        ListIterator<MethodObject> mi = getMethodIterator();
        while(mi.hasNext()) {
            MethodObject mo = mi.next();
            if(mo.equals(smio))
                return mo;
        }
        return null;
    }

    public boolean containsMethodInvocation(MethodInvocationObject methodInvocation) {
    	for(MethodObject method : methodList) {
    		if(method.containsMethodInvocation(methodInvocation))
    			return true;
    	}
    	return false;
    }

    public boolean containsFieldInstruction(FieldInstructionObject fieldInstruction) {
    	for(MethodObject method : methodList) {
    		if(method.containsFieldInstruction(fieldInstruction))
    			return true;
    	}
    	return false;
    }

    public boolean containsMethodInvocation(MethodInvocationObject methodInvocation, MethodObject excludedMethod) {
    	for(MethodObject method : methodList) {
    		if(!method.equals(excludedMethod) && method.containsMethodInvocation(methodInvocation))
    			return true;
    	}
    	return false;
    }

    public boolean containsSuperMethodInvocation(SuperMethodInvocationObject superMethodInvocation) {
    	for(MethodObject method : methodList) {
    		if(method.containsSuperMethodInvocation(superMethodInvocation))
    			return true;
    	}
    	return false;
    }

    public boolean hasFieldType(String className) {
        ListIterator<FieldObject> fi = getFieldIterator();
        while(fi.hasNext()) {
            FieldObject fo = fi.next();
            if(fo.getType().getClassType().equals(className))
                return true;
        }
        return false;
    }

	public Set<FieldObject> getFieldsAccessedInsideMethod(AbstractMethodDeclaration method) {
		Set<FieldObject> fields = new LinkedHashSet<FieldObject>();
		for(FieldInstructionObject fieldInstruction : method.getFieldInstructions()) {
			FieldObject accessedFieldFromThisClass = findField(fieldInstruction);
			if(accessedFieldFromThisClass != null) {
				fields.add(accessedFieldFromThisClass);
			}
		}
		if(method.getMethodBody() != null) {
			List<TryStatementObject> tryStatements = method.getMethodBody().getTryStatements();
			for(TryStatementObject tryStatement : tryStatements) {
				for(CatchClauseObject catchClause : tryStatement.getCatchClauses()) {
					for(FieldInstructionObject fieldInstruction : catchClause.getBody().getFieldInstructions()) {
						FieldObject accessedFieldFromThisClass = findField(fieldInstruction);
						if(accessedFieldFromThisClass != null) {
							fields.add(accessedFieldFromThisClass);
						}
					}
				}
				if(tryStatement.getFinallyClause() != null) {
					for(FieldInstructionObject fieldInstruction : tryStatement.getFinallyClause().getFieldInstructions()) {
						FieldObject accessedFieldFromThisClass = findField(fieldInstruction);
						if(accessedFieldFromThisClass != null) {
							fields.add(accessedFieldFromThisClass);
						}
					}
				}
			}
		}
		return fields;
	}

	public FieldObject getField(FieldInstructionObject fieldInstruction) {
		for(FieldObject field : fieldList) {
			if(field.equals(fieldInstruction)) {
				return field;
			}
		}
		return null;
	}
	
	protected FieldObject findField(FieldInstructionObject fieldInstruction) {
		FieldObject field = getField(fieldInstruction);
		if(field != null) {
			return field;
		}
		else {
			TypeObject superclassType = getSuperclass();
			if(superclassType != null) {
				ClassObject superclassObject = ASTReader.getSystemObject().getClassObject(superclassType.toString());
				if(superclassObject != null) {
					return superclassObject.findField(fieldInstruction);
				}
			}
		}
		return null;
	}
}
