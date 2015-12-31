/**
 * 
 */
package soottocfg.soot.memory_model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Verify;

import soot.SootClass;
import soot.SootField;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.StringConstant;
import soottocfg.cfg.Program;
import soottocfg.cfg.Variable;
import soottocfg.cfg.expression.ArrayLengthExpression;
import soottocfg.cfg.expression.BinaryExpression;
import soottocfg.cfg.expression.BinaryExpression.BinaryOperator;
import soottocfg.cfg.expression.Expression;
import soottocfg.cfg.expression.IdentifierExpression;
import soottocfg.cfg.expression.InstanceOfExpression;
import soottocfg.cfg.expression.IntegerLiteral;
import soottocfg.cfg.statement.AssumeStatement;
import soottocfg.cfg.type.BoolType;
import soottocfg.cfg.type.ClassSignature;
import soottocfg.cfg.type.IntType;
import soottocfg.cfg.type.MapType;
import soottocfg.cfg.type.ReferenceType;
import soottocfg.cfg.type.Type;
import soottocfg.soot.util.MethodInfo;
import soottocfg.soot.util.SootTranslationHelpers;

/**
 * @author schaef
 *
 */
public abstract class BasicMemoryModel extends MemoryModel {

	protected final Variable nullConstant;
	protected Program program;
	protected final Map<soot.Type, soottocfg.cfg.type.Type> types = new HashMap<soot.Type, soottocfg.cfg.type.Type>();
	protected final Map<SootField, Variable> fieldGlobals = new HashMap<SootField, Variable>();

	protected final Map<Constant, Variable> constantDictionary = new HashMap<Constant, Variable>();

	protected final Type nullType;

	public BasicMemoryModel() {
		this.program = SootTranslationHelpers.v().getProgram();

		nullType = new ReferenceType(null);
		this.nullConstant = this.program.loopupGlobalVariable("$null", nullType);
	}

	@Override
	public boolean isNullReference(Expression e) {
		return (e instanceof IdentifierExpression && ((IdentifierExpression) e).getVariable() == nullConstant);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkNewExpr(soot.jimple.NewExpr)
	 */
	@Override
	public Expression mkNewExpr(NewExpr arg0) {
		Type newType = this.lookupType(arg0.getBaseType());
		MethodInfo mi = this.statementSwitch.getMethodInto();
		Variable newLocal = mi.createFreshLocal("$new", newType, true, true);
		// add: assume newLocal!=null
		this.statementSwitch.push(new AssumeStatement(
				SootTranslationHelpers.v().getSourceLocation(this.statementSwitch.getCurrentStmt()),
				new BinaryExpression(this.statementSwitch.getCurrentLoc(), BinaryOperator.Ne, new IdentifierExpression(this.statementSwitch.getCurrentLoc(), newLocal), this.mkNullConstant())));
		// add: assume newLocal instanceof newType
		Expression instof = new InstanceOfExpression(this.statementSwitch.getCurrentLoc(), new IdentifierExpression(this.statementSwitch.getCurrentLoc(), newLocal),
				SootTranslationHelpers.v().lookupTypeVariable(arg0.getBaseType()));
		this.statementSwitch.push(
				new AssumeStatement(SootTranslationHelpers.v().getSourceLocation(this.statementSwitch.getCurrentStmt()),
						new BinaryExpression(this.statementSwitch.getCurrentLoc(), BinaryOperator.Ne, instof, IntegerLiteral.zero())));

		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), newLocal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkNewArrayExpr(soot.jimple.
	 * NewArrayExpr )
	 */
	@Override
	public Expression mkNewArrayExpr(NewArrayExpr arg0) {
		arg0.getSize().apply(valueSwitch);
		Expression sizeExpression = valueSwitch.popExpression();

		Type newType = this.lookupType(arg0.getType());
		MethodInfo mi = this.statementSwitch.getMethodInto();
		Variable newLocal = mi.createFreshLocal("$newArr", newType, true, true);

		this.statementSwitch.push(new AssumeStatement(
				SootTranslationHelpers.v().getSourceLocation(this.statementSwitch.getCurrentStmt()),
				new BinaryExpression(this.statementSwitch.getCurrentLoc(), BinaryOperator.Ne, new IdentifierExpression(this.statementSwitch.getCurrentLoc(), newLocal), this.mkNullConstant())));

		this.statementSwitch.push(
				new AssumeStatement(SootTranslationHelpers.v().getSourceLocation(this.statementSwitch.getCurrentStmt()),
						new BinaryExpression(this.statementSwitch.getCurrentLoc(), BinaryOperator.Eq,
								new ArrayLengthExpression(this.statementSwitch.getCurrentLoc(), new IdentifierExpression(this.statementSwitch.getCurrentLoc(), newLocal)), sizeExpression)));

		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), newLocal);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.soot.memory_model.MemoryModel#mkNewMultiArrayExpr(soot.jimple
	 * .NewMultiArrayExpr)
	 */
	@Override
	public Expression mkNewMultiArrayExpr(NewMultiArrayExpr arg0) {
		// TODO Auto-generated method stub
		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), 
				SootTranslationHelpers.v().getProgram().createFreshGlobal("TODO", lookupType(arg0.getType())));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * jayhorn.soot.memory_model.MemoryModel#mkArrayRefExpr(soot.jimple.ArrayRef
	 * )
	 */
	@Override
	public Expression mkArrayRefExpr(ArrayRef arg0) {
		// TODO Auto-generated method stub
		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), 
				SootTranslationHelpers.v().getProgram().createFreshGlobal("TODO", IntType.instance()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkStringLengthExpr(soot.Value)
	 */
	@Override
	public Expression mkStringLengthExpr(Value arg0) {
		// TODO Auto-generated method stub
		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), 
				SootTranslationHelpers.v().getProgram().createFreshGlobal("TODO", IntType.instance()));
	}

	protected Variable lookupStaticField(SootField field) {
		Verify.verify(false);
		return this.program.loopupGlobalVariable(field.getName(), lookupType(field.getType()));
	}
	
	protected Variable lookupField(SootField field) {
		if (!this.fieldGlobals.containsKey(field)) {
			final String fieldName = field.getDeclaringClass().getName() + "." + field.getName();
			Variable fieldVar = this.program.loopupGlobalVariable(fieldName, this.lookupType(field.getType()));
			this.fieldGlobals.put(field, fieldVar);
		}
		return this.fieldGlobals.get(field);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkNullConstant()
	 */
	@Override
	public Expression mkNullConstant() {
		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), nullConstant);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkStringConstant(soot.jimple.
	 * StringConstant)
	 */
	@Override
	public Expression mkStringConstant(StringConstant arg0) {
		if (!constantDictionary.containsKey(arg0)) {
			constantDictionary.put(arg0, SootTranslationHelpers.v().getProgram().loopupGlobalVariable(
					"$string" + constantDictionary.size(), lookupType(arg0.getType()), true, true));
		}
		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), constantDictionary.get(arg0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkDoubleConstant(soot.jimple.
	 * DoubleConstant)
	 */
	@Override
	public Expression mkDoubleConstant(DoubleConstant arg0) {
		if (!constantDictionary.containsKey(arg0)) {
			constantDictionary.put(arg0, SootTranslationHelpers.v().getProgram().loopupGlobalVariable(
					"$double" + constantDictionary.size(), lookupType(arg0.getType()), true, true));
		}
		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), constantDictionary.get(arg0));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jayhorn.soot.memory_model.MemoryModel#mkFloatConstant(soot.jimple.
	 * FloatConstant)
	 */
	@Override
	public Expression mkFloatConstant(FloatConstant arg0) {
		if (!constantDictionary.containsKey(arg0)) {
			constantDictionary.put(arg0, SootTranslationHelpers.v().getProgram().loopupGlobalVariable(
					"$float" + constantDictionary.size(), lookupType(arg0.getType()), true, true));
		}
		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), constantDictionary.get(arg0));
	}

	@Override
	public Expression lookupClassConstant(ClassConstant arg0) {
		if (!constantDictionary.containsKey(arg0)) {
			constantDictionary.put(arg0, SootTranslationHelpers.v().getProgram().loopupGlobalVariable(
					"$classconst" + constantDictionary.size(), lookupType(arg0.getType()), true, true));
		}
		return new IdentifierExpression(this.statementSwitch.getCurrentLoc(), constantDictionary.get(arg0));
	}

	@Override
	public Type lookupType(soot.Type t) {
		if (!types.containsKey(t)) {
			Type type = null;
			if (t instanceof soot.BooleanType) {
				type = BoolType.instance();
			} else if (t instanceof soot.ByteType) {				
				type = IntType.instance();
			} else if (t instanceof soot.CharType) {
				//TODO
				type = IntType.instance();
			} else if (t instanceof soot.DoubleType) {
				//TODO
				type = IntType.instance();
			} else if (t instanceof soot.FloatType) {
				//TODO
				type = IntType.instance();
			} else if (t instanceof soot.IntType) {
				//TODO
				type = IntType.instance();
			} else if (t instanceof soot.LongType) {
				//TODO
				type = IntType.instance();
			} else if (t instanceof soot.ShortType) {
				//TODO
				type = IntType.instance();
			} else if (t instanceof soot.ArrayType) {
				soot.ArrayType at = (soot.ArrayType) t;
				Type baseType = lookupType(at.baseType);
				List<Type> ids = new LinkedList<Type>();
				for (int i = 0; i < at.numDimensions; i++) {
					ids.add(IntType.instance());
				}
				type = new MapType(ids, baseType);
			} else if (t instanceof soot.NullType) {
				return this.nullConstant.getType();
			} else if (t instanceof soot.RefType) {
				soot.RefType rt = (soot.RefType) t;
				ClassSignature cc = lookupClassSignature(rt.getSootClass());
				type = new ReferenceType(cc);
			} else {
				throw new RuntimeException("Don't know what to do with type " + t);
			}
			types.put(t, type);
		}
		return types.get(t);
	}

	private Map<SootClass, ClassSignature> classSignatures = new HashMap<SootClass, ClassSignature>();

	public ClassSignature lookupClassSignature(SootClass c) {
		if (!classSignatures.containsKey(c)) {
			Collection<ClassSignature> parents = new HashSet<ClassSignature>();
			if (c.resolvingLevel() >= SootClass.HIERARCHY) {
				if (c.hasSuperclass()) {
					parents.add(lookupClassSignature(c.getSuperclass()));
				}
			} else {
				// TODO
			}
			classSignatures.put(c, new ClassSignature(c.getJavaStyleName(), parents));
			// add the fields after that to avoid endless loop.
			if (c.resolvingLevel() >= SootClass.SIGNATURES) {
				List<Variable> fields = new LinkedList<Variable>();
				for (SootField f : c.getFields()) {
					fields.add(lookupField(f));
				}
				classSignatures.get(c).setAssociatedFields(fields);
			} else {
				// TODO
			}
		}
		return classSignatures.get(c);
	}

}