package io.smallrye.openapi.api.models.media;

import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_ADDITIONAL_PROPERTIES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_ALL_OF;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_ANY_OF;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_COMMENT;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_CONST;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_CONTAINS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_CONTENT_ENCODING;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_CONTENT_MEDIA_TYPE;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_CONTENT_SCHEMA;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_DEFAULT;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_DEPENDENT_REQUIRED;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_DEPENDENT_SCHEMAS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_DEPRECATED;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_DESCRIPTION;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_DISCRIMINATOR;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_ELSE;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_ENUM;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_EXAMPLE;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_EXAMPLES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_EXCLUSIVE_MAXIMUM;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_EXCLUSIVE_MINIMUM;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_FORMAT;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_IF;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_ITEMS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MAXIMUM;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MAX_CONTAINS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MAX_ITEMS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MAX_LENGTH;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MAX_PROPERTIES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MINIMUM;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MIN_CONTAINS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MIN_ITEMS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MIN_LENGTH;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MIN_PROPERTIES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_MULTIPLE_OF;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_NOT;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_ONE_OF;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_PATTERN;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_PATTERN_PROPERTIES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_PREFIX_ITEMS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_PROPERTIES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_PROPERTY_NAMES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_READ_ONLY;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_REQUIRED;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_SCHEMA_DIALECT;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_THEN;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_TYPE;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_UNEVALUATED_ITEMS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_UNEVALUATED_PROPERTIES;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_UNIQUE_ITEMS;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_WRITE_ONLY;
import static io.smallrye.openapi.runtime.io.schema.SchemaConstant.PROP_XML;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.XML;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.smallrye.openapi.api.constants.OpenApiConstants;
import io.smallrye.openapi.api.models.JsonWrappingImpl;
import io.smallrye.openapi.api.models.ModelImpl;
import io.smallrye.openapi.api.models.NodeObjectCache;
import io.smallrye.openapi.runtime.io.JsonUtil;
import io.smallrye.openapi.runtime.io.Referenceable;
import io.smallrye.openapi.runtime.io.discriminator.DiscriminatorReader;
import io.smallrye.openapi.runtime.io.discriminator.DiscriminatorWriter;
import io.smallrye.openapi.runtime.io.externaldocs.ExternalDocsConstant;
import io.smallrye.openapi.runtime.io.externaldocs.ExternalDocsReader;
import io.smallrye.openapi.runtime.io.externaldocs.ExternalDocsWriter;
import io.smallrye.openapi.runtime.io.schema.SchemaConstant;
import io.smallrye.openapi.runtime.io.xml.XmlReader;
import io.smallrye.openapi.runtime.io.xml.XmlWriter;
import io.smallrye.openapi.runtime.util.ModelUtil;

/**
 * An implementation of the {@link Schema} OpenAPI model interface.
 */
public class SchemaImpl extends JsonWrappingImpl implements Schema, ModelImpl {

    private static final NodeObjectCache<ObjectNode, SchemaImpl> NODE_OBJECT_CACHE = new NodeObjectCache<>(SchemaImpl::new);

    /**
     * Get or create a schema object for a given JSON object
     * <p>
     * If a schema object for this JSON node already exists, the same object will be returned, otherwise a new one may be
     * created.
     *
     * @param node the JSON object
     * @return the schema object
     */
    public static SchemaImpl getOrCreateFromNode(ObjectNode node) {
        return NODE_OBJECT_CACHE.getOrCreate(node);
    }

    /**
     * Get a boolean schema object
     * <p>
     * Calling this method twice for the same boolean value will return the same (immutable) schema object
     *
     * @param booleanValue the schema value
     * @return an immutable boolean schema
     */
    public static SchemaImpl ofBoolean(boolean booleanValue) {
        return booleanValue ? TRUE_SCHEMA : FALSE_SCHEMA;
    }

    private static final SchemaImpl TRUE_SCHEMA = new SchemaImpl(true);
    private static final SchemaImpl FALSE_SCHEMA = new SchemaImpl(false);

    // Non-standard
    private String name;
    private int modCount;
    private List<Schema> typeObservers;

    /**
     * The boolean value of this schema. {@code null} in most cases where the schema is an object
     */
    private Boolean booleanValue;

    @Override
    public JsonWrappingImpl mergeFrom(JsonWrappingImpl other) {
        SchemaImpl otherSchema = null;

        // If either schema is a boolean, we don't merge and just return the other schema
        if (this.isBooleanSchema()) {
            return other;
        }
        if (other instanceof SchemaImpl && ((SchemaImpl) other).isBooleanSchema()) {
            return otherSchema;
        }

        // Otherwise we merge the JSON trees
        return super.mergeFrom(other);
    }

    public static boolean isNamed(Schema schema) {
        return schema instanceof SchemaImpl && ((SchemaImpl) schema).name != null;
    }

    public static int getModCount(Schema schema) {
        return schema instanceof SchemaImpl ? ((SchemaImpl) schema).modCount : -1;
    }

    public static void addTypeObserver(Schema observable, Schema observer) {
        if (observable instanceof SchemaImpl) {
            SchemaImpl obs = (SchemaImpl) observable;
            obs.typeObservers = ModelUtil.add(observer, obs.typeObservers, ArrayList<Schema>::new);
        }

        observer.setType(observable.getType());
    }

    public static SchemaImpl copyOf(Schema other) {
        if (other == null) {
            return new SchemaImpl();
        }
        if (other instanceof SchemaImpl) {
            SchemaImpl otherImpl = (SchemaImpl) other;
            SchemaImpl clone;
            if (otherImpl.booleanValue != null) {
                // Boolean schemas are singletons and immutable, no need to actually copy
                clone = otherImpl;
            } else {
                clone = getOrCreateFromNode(otherImpl.node.deepCopy());
            }
            clone.name = otherImpl.name;
            return clone;
        }
        throw new UnsupportedOperationException("Can't copy a different impl");
        //        SchemaImpl clone = (SchemaImpl) MergeUtil.mergeObjects(new SchemaImpl(), other);
        //        clone.required = copy(clone.required, () -> new ArrayList<>(clone.required));
        //        clone.enumeration = copy(clone.enumeration, () -> new ArrayList<>(clone.enumeration));
        //        clone.items = copy(clone.items, () -> copyOf(clone.items));
        //
        //        clone.allOf = copy(clone.allOf, () -> clone.allOf
        //                .stream()
        //                .map(SchemaImpl::copyOf)
        //                .collect(Collectors.toList()));
        //
        //        clone.properties = copy(clone.properties, () -> clone.properties.entrySet()
        //                .stream()
        //                .collect(Collectors.toMap(
        //                        Map.Entry::getKey,
        //                        e -> copyOf(e.getValue()),
        //                        (u, v) -> {
        //                            throw new IllegalStateException(String.format("Duplicate key %s", u));
        //                        },
        //                        LinkedHashMap::new)));
        //
        //        clone.additionalPropertiesSchema = copy(clone.additionalPropertiesSchema,
        //                () -> copyOf(clone.additionalPropertiesSchema));
        //
        //        clone.xml = copy(clone.xml, () -> MergeUtil.mergeObjects(new XMLImpl(), clone.xml));
        //        clone.externalDocs = copy(clone.externalDocs,
        //                () -> MergeUtil.mergeObjects(new ExternalDocumentationImpl(), clone.externalDocs));
        //
        //        clone.oneOf = copy(clone.oneOf, () -> clone.oneOf
        //                .stream()
        //                .map(SchemaImpl::copyOf)
        //                .collect(Collectors.toList()));
        //
        //        clone.anyOf = copy(clone.anyOf, () -> clone.anyOf
        //                .stream()
        //                .map(SchemaImpl::copyOf)
        //                .collect(Collectors.toList()));
        //
        //        clone.not = copy(clone.not, () -> copyOf(clone.not));
        //
        //        return clone;
    }

    //    private static <T> T copy(T property, Supplier<T> copySupplier) {
    //        if (property != null) {
    //            return copySupplier.get();
    //        }
    //        return null;
    //    }

    /**
     * Create an empty named schema
     *
     * @param name the name
     */
    public SchemaImpl(String name) {
        super(JsonUtil.objectNode());
        this.name = name;
        NODE_OBJECT_CACHE.put(this.node, this);
    }

    /**
     * Create an empty schema
     */
    public SchemaImpl() {
        this((String) null);
    }

    /**
     * Create a schema from a boolean value
     * <p>
     * External callers should use {@link #ofBoolean(boolean)}
     *
     * @param booleanValue the boolean value
     */
    private SchemaImpl(boolean booleanValue) {
        super(null);
        this.booleanValue = booleanValue;
    }

    /**
     * Create a schema from a JSON object
     * <p>
     * External callers should use {@link #getOrCreateFromNode(ObjectNode)} instead
     *
     * @param node the json object
     */
    private SchemaImpl(ObjectNode node) {
        super(node);
    }

    public String getName() {
        return name;
    }

    private void incrementModCount() {
        modCount++;
    }

    public JsonNode getJsonNode() {
        if (isBooleanSchema()) {
            return JsonNodeFactory.instance.booleanNode(booleanValue);
        } else {
            return node;
        }
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.Reference#getRef()
     */
    @Override
    public String getRef() {
        return getProperty(Referenceable.PROP_$REF, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.Reference#setRef(java.lang.String)
     */
    @Override
    public void setRef(String ref) {
        if (ref != null && !ref.contains("/")) {
            ref = OpenApiConstants.REF_PREFIX_SCHEMA + ref;
        }
        incrementModCount();
        setProperty(Referenceable.PROP_$REF, ref, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getDiscriminator()
     */
    @Override
    public Discriminator getDiscriminator() {
        return getProperty(PROP_DISCRIMINATOR, DISCRIMINATOR_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setDiscriminator(org.eclipse.microprofile.openapi.models.media.Discriminator)
     */
    @Override
    public void setDiscriminator(Discriminator discriminator) {
        incrementModCount();
        setProperty(PROP_DISCRIMINATOR, discriminator, DISCRIMINATOR_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getTitle()
     */
    @Override
    public String getTitle() {
        return getProperty(SchemaConstant.PROP_TITLE, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        incrementModCount();
        setProperty(SchemaConstant.PROP_TITLE, title, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getDefaultValue()
     */
    @Override
    public Object getDefaultValue() {
        return getProperty(PROP_DEFAULT, OBJECT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setDefaultValue(java.lang.Object)
     */
    @Override
    public void setDefaultValue(Object defaultValue) {
        incrementModCount();
        setProperty(SchemaConstant.PROP_DEFAULT, defaultValue, OBJECT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getEnumeration()
     */
    @Override
    public List<Object> getEnumeration() {
        return getListProperty(PROP_ENUM, OBJECT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setEnumeration(java.util.List)
     */
    @Override
    public void setEnumeration(List<Object> enumeration) {
        incrementModCount();
        setListProperty(PROP_ENUM, enumeration, OBJECT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#addEnumeration(java.lang.Object)
     */
    @Override
    public Schema addEnumeration(Object enumeration) {
        incrementModCount();
        addToListProperty(PROP_ENUM, enumeration, OBJECT_CONVERTER);
        return this;
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#removeEnumeration(Object)
     */
    @Override
    public void removeEnumeration(Object enumeration) {
        incrementModCount();
        removeFromListProperty(PROP_ENUM, enumeration, OBJECT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMultipleOf()
     */
    @Override
    public BigDecimal getMultipleOf() {
        return getProperty(PROP_MULTIPLE_OF, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMultipleOf(java.math.BigDecimal)
     */
    @Override
    public void setMultipleOf(BigDecimal multipleOf) {
        incrementModCount();
        setProperty(PROP_MULTIPLE_OF, multipleOf, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMaximum()
     */
    @Override
    public BigDecimal getMaximum() {
        return getProperty(PROP_MAXIMUM, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMaximum(java.math.BigDecimal)
     */
    @Override
    public void setMaximum(BigDecimal maximum) {
        incrementModCount();
        setProperty(PROP_MAXIMUM, maximum, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getExclusiveMaximum()
     */
    @Override
    public BigDecimal getExclusiveMaximum() {
        return getProperty(PROP_EXCLUSIVE_MAXIMUM, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setExclusiveMaximum(java.lang.Boolean)
     */
    @Override
    public void setExclusiveMaximum(BigDecimal exclusiveMaximum) {
        incrementModCount();
        setProperty(PROP_EXCLUSIVE_MAXIMUM, exclusiveMaximum, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMinimum()
     */
    @Override
    public BigDecimal getMinimum() {
        return getProperty(PROP_MINIMUM, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMinimum(java.math.BigDecimal)
     */
    @Override
    public void setMinimum(BigDecimal minimum) {
        incrementModCount();
        setProperty(PROP_MINIMUM, minimum, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getExclusiveMinimum()
     */
    @Override
    public BigDecimal getExclusiveMinimum() {
        return getProperty(PROP_EXCLUSIVE_MINIMUM, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setExclusiveMinimum(java.lang.Boolean)
     */
    @Override
    public void setExclusiveMinimum(BigDecimal exclusiveMinimum) {
        incrementModCount();
        setProperty(PROP_EXCLUSIVE_MINIMUM, exclusiveMinimum, NUMBER_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMaxLength()
     */
    @Override
    public Integer getMaxLength() {
        return getProperty(PROP_MAX_LENGTH, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMaxLength(java.lang.Integer)
     */
    @Override
    public void setMaxLength(Integer maxLength) {
        incrementModCount();
        setProperty(PROP_MAX_LENGTH, maxLength, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMinLength()
     */
    @Override
    public Integer getMinLength() {
        return getProperty(PROP_MIN_LENGTH, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMinLength(java.lang.Integer)
     */
    @Override
    public void setMinLength(Integer minLength) {
        incrementModCount();
        setProperty(PROP_MIN_LENGTH, minLength, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getPattern()
     */
    @Override
    public String getPattern() {
        return getProperty(PROP_PATTERN, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setPattern(java.lang.String)
     */
    @Override
    public void setPattern(String pattern) {
        incrementModCount();
        setProperty(PROP_PATTERN, pattern, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMaxItems()
     */
    @Override
    public Integer getMaxItems() {
        return getProperty(PROP_MAX_ITEMS, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMaxItems(java.lang.Integer)
     */
    @Override
    public void setMaxItems(Integer maxItems) {
        incrementModCount();
        setProperty(PROP_MAX_ITEMS, maxItems, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMinItems()
     */
    @Override
    public Integer getMinItems() {
        return getProperty(PROP_MIN_ITEMS, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMinItems(java.lang.Integer)
     */
    @Override
    public void setMinItems(Integer minItems) {
        incrementModCount();
        setProperty(PROP_MIN_ITEMS, minItems, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getUniqueItems()
     */
    @Override
    public Boolean getUniqueItems() {
        return getProperty(PROP_UNIQUE_ITEMS, BOOLEAN_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setUniqueItems(java.lang.Boolean)
     */
    @Override
    public void setUniqueItems(Boolean uniqueItems) {
        incrementModCount();
        setProperty(PROP_UNIQUE_ITEMS, uniqueItems, BOOLEAN_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMaxProperties()
     */
    @Override
    public Integer getMaxProperties() {
        return getProperty(PROP_MAX_PROPERTIES, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMaxProperties(java.lang.Integer)
     */
    @Override
    public void setMaxProperties(Integer maxProperties) {
        incrementModCount();
        setProperty(PROP_MAX_PROPERTIES, maxProperties, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getMinProperties()
     */
    @Override
    public Integer getMinProperties() {
        return getProperty(PROP_MIN_PROPERTIES, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setMinProperties(java.lang.Integer)
     */
    @Override
    public void setMinProperties(Integer minProperties) {
        incrementModCount();
        setProperty(PROP_MIN_PROPERTIES, minProperties, INT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getRequired()
     */
    @Override
    public List<String> getRequired() {
        return getListProperty(PROP_REQUIRED, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setRequired(java.util.List)
     */
    @Override
    public void setRequired(List<String> required) {
        incrementModCount();
        setListProperty(PROP_REQUIRED, required, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#addRequired(java.lang.String)
     */
    @Override
    public Schema addRequired(String required) {
        incrementModCount();
        addToListProperty(PROP_REQUIRED, required, STRING_CONVERTER);
        return this;
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#removeRequired(String)
     */
    @Override
    public void removeRequired(String required) {
        incrementModCount();
        removeFromListProperty(PROP_REQUIRED, required, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getType()
     */
    @Override
    public List<SchemaType> getType() {
        List<SchemaType> resultList = getListProperty(PROP_TYPE, SCHEMA_TYPE_CONVERTER);
        if (resultList != null) {
            return resultList;
        }

        SchemaType result = getProperty(PROP_TYPE, SCHEMA_TYPE_CONVERTER);
        if (result != null) {
            return Collections.singletonList(result);
        }

        return null;
    }

    @Override
    public void setType(List<SchemaType> types) {
        incrementModCount();
        setListProperty(PROP_TYPE, types, SCHEMA_TYPE_CONVERTER);

        if (typeObservers != null) {
            typeObservers.forEach(o -> o.setType(types));
        }
    }

    @Override
    public Schema addType(SchemaType type) {
        incrementModCount();
        addToListProperty(PROP_TYPE, type, SCHEMA_TYPE_CONVERTER);

        if (typeObservers != null) {
            typeObservers.forEach(o -> o.addType(type));
        }
        return this;
    }

    @Override
    public void removeType(SchemaType type) {
        incrementModCount();
        removeFromListProperty(PROP_TYPE, type, SCHEMA_TYPE_CONVERTER);

        if (typeObservers != null) {
            typeObservers.forEach(o -> o.removeType(type));
        }
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setType(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType)
     */
    @SuppressWarnings("deprecation")
    @Override
    public void setType(SchemaType type) {
        incrementModCount();
        List<SchemaType> currentValue = getListProperty(PROP_TYPE, SCHEMA_TYPE_CONVERTER);
        if (currentValue != null && currentValue.contains(SchemaType.NULL)) {
            if (type == null) {
                setListProperty(PROP_TYPE, Arrays.asList(SchemaType.NULL), SCHEMA_TYPE_CONVERTER);
            } else {
                setListProperty(PROP_TYPE, Arrays.asList(type, SchemaType.NULL), SCHEMA_TYPE_CONVERTER);
            }
        } else {
            if (type == null) {
                setListProperty(PROP_TYPE, null, SCHEMA_TYPE_CONVERTER);
            } else {
                setListProperty(PROP_TYPE, Collections.singletonList(type), SCHEMA_TYPE_CONVERTER);
            }
        }

        if (typeObservers != null) {
            typeObservers.forEach(o -> o.setType(type));
        }
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getNot()
     */
    @Override
    public Schema getNot() {
        return getProperty(PROP_NOT, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setNot(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public void setNot(Schema not) {
        incrementModCount();
        setProperty(PROP_NOT, not, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getProperties()
     */
    @Override
    public Map<String, Schema> getProperties() {
        return getMapProperty(PROP_PROPERTIES, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setProperties(java.util.Map)
     */
    @Override
    public void setProperties(Map<String, Schema> properties) {
        incrementModCount();
        setMapProperty(PROP_PROPERTIES, properties, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#addProperty(java.lang.String,
     *      org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public Schema addProperty(String key, Schema propertySchema) {
        incrementModCount();
        addToMapProperty(PROP_PROPERTIES, key, propertySchema, SCHEMA_CONVERTER);
        return this;
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#removeProperty(java.lang.String)
     */
    @Override
    public void removeProperty(String key) {
        incrementModCount();
        removeFromMapProperty(PROP_PROPERTIES, key);
    }

    @Override
    public Schema getAdditionalPropertiesSchema() {
        return getProperty(PROP_ADDITIONAL_PROPERTIES, SCHEMA_CONVERTER);
    }

    @Override
    public Boolean getAdditionalPropertiesBoolean() {
        Schema additionalPropertiesSchema = getAdditionalPropertiesSchema();
        return additionalPropertiesSchema == null ? null : additionalPropertiesSchema.getBooleanSchema();
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setAdditionalPropertiesSchema(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public void setAdditionalPropertiesSchema(Schema additionalProperties) {
        incrementModCount();
        setProperty(PROP_ADDITIONAL_PROPERTIES, additionalProperties, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setAdditionalPropertiesBoolean(java.lang.Boolean)
     */
    @Override
    public void setAdditionalPropertiesBoolean(Boolean additionalProperties) {
        incrementModCount();
        if (additionalProperties != null) {
            setAdditionalPropertiesSchema(new SchemaImpl(additionalProperties));
        } else {
            setAdditionalPropertiesSchema(null);
        }
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getDescription()
     */
    @Override
    public String getDescription() {
        return getProperty(PROP_DESCRIPTION, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description) {
        incrementModCount();
        setProperty(PROP_DESCRIPTION, description, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getFormat()
     */
    @Override
    public String getFormat() {
        return getProperty(PROP_FORMAT, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setFormat(java.lang.String)
     */
    @Override
    public void setFormat(String format) {
        incrementModCount();
        setProperty(PROP_FORMAT, format, STRING_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getNullable()
     */
    @Override
    public Boolean getNullable() {
        List<SchemaType> types = getType();
        return types != null ? types.contains(SchemaType.NULL) : Boolean.FALSE;
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setNullable(java.lang.Boolean)
     */
    @Override
    public void setNullable(Boolean nullable) {
        incrementModCount();
        if (nullable == Boolean.TRUE) {
            List<SchemaType> types = getType();
            if (types == null || !types.contains(SchemaType.NULL)) {
                addType(SchemaType.NULL);
            }
        } else {
            removeType(SchemaType.NULL);
        }
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getReadOnly()
     */
    @Override
    public Boolean getReadOnly() {
        return getProperty(PROP_READ_ONLY, BOOLEAN_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setReadOnly(java.lang.Boolean)
     */
    @Override
    public void setReadOnly(Boolean readOnly) {
        incrementModCount();
        setProperty(PROP_READ_ONLY, readOnly, BOOLEAN_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getWriteOnly()
     */
    @Override
    public Boolean getWriteOnly() {
        return getProperty(PROP_WRITE_ONLY, BOOLEAN_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setWriteOnly(java.lang.Boolean)
     */
    @Override
    public void setWriteOnly(Boolean writeOnly) {
        incrementModCount();
        setProperty(PROP_WRITE_ONLY, writeOnly, BOOLEAN_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getExample()
     */
    @Override
    public Object getExample() {
        return getProperty(PROP_EXAMPLE, OBJECT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setExample(java.lang.Object)
     */
    @Override
    public void setExample(Object example) {
        incrementModCount();
        setProperty(PROP_EXAMPLE, example, OBJECT_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getExternalDocs()
     */
    @Override
    public ExternalDocumentation getExternalDocs() {
        return getProperty(ExternalDocsConstant.PROP_EXTERNAL_DOCS, EXTERNAL_DOCUMENTATION_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setExternalDocs(org.eclipse.microprofile.openapi.models.ExternalDocumentation)
     */
    @Override
    public void setExternalDocs(ExternalDocumentation externalDocs) {
        incrementModCount();
        setProperty(ExternalDocsConstant.PROP_EXTERNAL_DOCS, externalDocs, EXTERNAL_DOCUMENTATION_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getDeprecated()
     */
    @Override
    public Boolean getDeprecated() {
        return getProperty(PROP_DEPRECATED, BOOLEAN_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setDeprecated(java.lang.Boolean)
     */
    @Override
    public void setDeprecated(Boolean deprecated) {
        incrementModCount();
        setProperty(PROP_DEPRECATED, deprecated, BOOLEAN_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getXml()
     */
    @Override
    public XML getXml() {
        return getProperty(PROP_XML, XML_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setXml(org.eclipse.microprofile.openapi.models.media.XML)
     */
    @Override
    public void setXml(XML xml) {
        incrementModCount();
        setProperty(PROP_XML, xml, XML_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getItems()
     */
    @Override
    public Schema getItems() {
        return getProperty(PROP_ITEMS, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setItems(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public void setItems(Schema items) {
        incrementModCount();
        setProperty(PROP_ITEMS, items, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getAllOf()
     */
    @Override
    public List<Schema> getAllOf() {
        return getListProperty(PROP_ALL_OF, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setAllOf(java.util.List)
     */
    @Override
    public void setAllOf(List<Schema> allOf) {
        incrementModCount();
        setListProperty(PROP_ALL_OF, allOf, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#addAllOf(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public Schema addAllOf(Schema allOf) {
        incrementModCount();
        addToListProperty(PROP_ALL_OF, allOf, SCHEMA_CONVERTER);
        return this;
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#removeAllOf(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public void removeAllOf(Schema allOf) {
        incrementModCount();
        removeFromListProperty(PROP_ALL_OF, allOf, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getAnyOf()
     */
    @Override
    public List<Schema> getAnyOf() {
        return getListProperty(PROP_ANY_OF, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setAnyOf(java.util.List)
     */
    @Override
    public void setAnyOf(List<Schema> anyOf) {
        incrementModCount();
        setListProperty(PROP_ANY_OF, anyOf, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#addAnyOf(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public Schema addAnyOf(Schema anyOf) {
        incrementModCount();
        addToListProperty(PROP_ANY_OF, anyOf, SCHEMA_CONVERTER);
        return this;
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#removeAnyOf(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public void removeAnyOf(Schema anyOf) {
        incrementModCount();
        removeFromListProperty(PROP_ANY_OF, anyOf, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#getOneOf()
     */
    @Override
    public List<Schema> getOneOf() {
        return getListProperty(PROP_ONE_OF, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#setOneOf(java.util.List)
     */
    @Override
    public void setOneOf(List<Schema> oneOf) {
        incrementModCount();
        setListProperty(PROP_ONE_OF, oneOf, SCHEMA_CONVERTER);
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#addOneOf(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public Schema addOneOf(Schema oneOf) {
        incrementModCount();
        addToListProperty(PROP_ONE_OF, oneOf, SCHEMA_CONVERTER);
        return this;
    }

    /**
     * @see org.eclipse.microprofile.openapi.models.media.Schema#removeOneOf(org.eclipse.microprofile.openapi.models.media.Schema)
     */
    @Override
    public void removeOneOf(Schema oneOf) {
        incrementModCount();
        removeFromListProperty(PROP_ONE_OF, oneOf, SCHEMA_CONVERTER);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getExtensions() {
        return (Map<String, Object>) OBJECT_CONVERTER.fromNode(node);
    }

    @Override
    public Schema addExtension(String name, Object value) {
        setProperty(name, value, OBJECT_CONVERTER);
        return this;
    }

    @Override
    public void removeExtension(String name) {
        node.remove(name);
    }

    @Override
    public void setExtensions(Map<String, Object> extensions) {
        if (extensions == null) {
            return;
        }
        for (Entry<String, Object> entry : extensions.entrySet()) {
            if (entry.getKey() != null) {
                setProperty(entry.getKey(), entry.getValue(), OBJECT_CONVERTER);
            }
        }
    }

    @Override
    public String getSchemaDialect() {
        return getProperty(PROP_SCHEMA_DIALECT, STRING_CONVERTER);
    }

    @Override
    public void setSchemaDialect(String schemaDialect) {
        setProperty(PROP_SCHEMA_DIALECT, schemaDialect, STRING_CONVERTER);
    }

    @Override
    public String getComment() {
        return getProperty(PROP_COMMENT, STRING_CONVERTER);
    }

    @Override
    public void setComment(String comment) {
        setProperty(PROP_COMMENT, comment, STRING_CONVERTER);
    }

    @Override
    public Schema getIfSchema() {
        return getProperty(PROP_IF, SCHEMA_CONVERTER);
    }

    @Override
    public void setIfSchema(Schema ifSchema) {
        setProperty(PROP_IF, ifSchema, SCHEMA_CONVERTER);
    }

    @Override
    public Schema getThenSchema() {
        return getProperty(PROP_THEN, SCHEMA_CONVERTER);
    }

    @Override
    public void setThenSchema(Schema thenSchema) {
        setProperty(PROP_THEN, thenSchema, SCHEMA_CONVERTER);
    }

    @Override
    public Schema getElseSchema() {
        return getProperty(PROP_ELSE, SCHEMA_CONVERTER);
    }

    @Override
    public void setElseSchema(Schema elseSchema) {
        setProperty(PROP_ELSE, elseSchema, SCHEMA_CONVERTER);
    }

    @Override
    public Map<String, Schema> getDependentSchemas() {
        return getMapProperty(PROP_DEPENDENT_SCHEMAS, SCHEMA_CONVERTER);
    }

    @Override
    public void setDependentSchemas(Map<String, Schema> dependentSchemas) {
        setMapProperty(PROP_DEPENDENT_SCHEMAS, dependentSchemas, SCHEMA_CONVERTER);
    }

    @Override
    public Schema addDependentSchema(String propertyName, Schema schema) {
        addToMapProperty(PROP_DEPENDENT_SCHEMAS, propertyName, schema, SCHEMA_CONVERTER);
        return this;
    }

    @Override
    public void removeDependentSchema(String propertyName) {
        removeFromMapProperty(PROP_DEPENDENT_SCHEMAS, propertyName);
    }

    @Override
    public List<Schema> getPrefixItems() {
        return getListProperty(PROP_PREFIX_ITEMS, SCHEMA_CONVERTER);
    }

    @Override
    public void setPrefixItems(List<Schema> prefixItems) {
        setListProperty(PROP_PREFIX_ITEMS, prefixItems, SCHEMA_CONVERTER);
    }

    @Override
    public Schema addPrefixItem(Schema prefixItem) {
        addToListProperty(PROP_PREFIX_ITEMS, prefixItem, SCHEMA_CONVERTER);
        return this;
    }

    @Override
    public void removePrefixItem(Schema prefixItem) {
        removeFromListProperty(PROP_PREFIX_ITEMS, prefixItem, SCHEMA_CONVERTER);
    }

    @Override
    public Schema getContains() {
        return getProperty(PROP_CONTAINS, SCHEMA_CONVERTER);
    }

    @Override
    public void setContains(Schema contains) {
        setProperty(PROP_CONTAINS, contains, SCHEMA_CONVERTER);
    }

    @Override
    public Map<String, Schema> getPatternProperties() {
        return getMapProperty(PROP_PATTERN_PROPERTIES, SCHEMA_CONVERTER);
    }

    @Override
    public void setPatternProperties(Map<String, Schema> patternProperties) {
        setMapProperty(PROP_PATTERN_PROPERTIES, patternProperties, SCHEMA_CONVERTER);
    }

    @Override
    public Schema addPatternProperty(String regularExpression, Schema schema) {
        addToMapProperty(PROP_PATTERN_PROPERTIES, regularExpression, schema, SCHEMA_CONVERTER);
        return this;
    }

    @Override
    public void removePatternProperty(String regularExpression) {
        removeFromMapProperty(PROP_PATTERN_PROPERTIES, regularExpression);
    }

    @Override
    public Schema getPropertyNames() {
        return getProperty(PROP_PROPERTY_NAMES, SCHEMA_CONVERTER);
    }

    @Override
    public void setPropertyNames(Schema propertyNameSchema) {
        setProperty(PROP_PROPERTY_NAMES, propertyNameSchema, SCHEMA_CONVERTER);
    }

    @Override
    public Schema getUnevaluatedItems() {
        return getProperty(PROP_UNEVALUATED_ITEMS, SCHEMA_CONVERTER);
    }

    @Override
    public void setUnevaluatedItems(Schema unevaluatedItems) {
        setProperty(PROP_UNEVALUATED_ITEMS, unevaluatedItems, SCHEMA_CONVERTER);
    }

    @Override
    public Schema getUnevaluatedProperties() {
        return getProperty(PROP_UNEVALUATED_PROPERTIES, SCHEMA_CONVERTER);
    }

    @Override
    public void setUnevaluatedProperties(Schema unevaluatedProperties) {
        setProperty(PROP_UNEVALUATED_PROPERTIES, unevaluatedProperties, SCHEMA_CONVERTER);
    }

    @Override
    public Object getConstValue() {
        return getProperty(PROP_CONST, OBJECT_CONVERTER);
    }

    @Override
    public void setConstValue(Object constValue) {
        setProperty(PROP_CONST, constValue, OBJECT_CONVERTER);
    }

    @Override
    public Integer getMaxContains() {
        return getProperty(PROP_MAX_CONTAINS, INT_CONVERTER);
    }

    @Override
    public void setMaxContains(Integer maxContains) {
        setProperty(PROP_MAX_CONTAINS, maxContains, INT_CONVERTER);
    }

    @Override
    public Integer getMinContains() {
        return getProperty(PROP_MIN_CONTAINS, INT_CONVERTER);
    }

    @Override
    public void setMinContains(Integer minContains) {
        setProperty(PROP_MIN_CONTAINS, minContains, INT_CONVERTER);
    }

    @Override
    public Map<String, List<String>> getDependentRequired() {
        return getMapProperty(PROP_DEPENDENT_REQUIRED, LIST_OF_STRING_CONVERTER);
    }

    @Override
    public void setDependentRequired(Map<String, List<String>> dependentRequired) {
        setMapProperty(PROP_DEPENDENT_REQUIRED, dependentRequired, LIST_OF_STRING_CONVERTER);
    }

    @Override
    public Schema addDependentRequired(String propertyName, List<String> additionalRequiredPropertyNames) {
        addToMapProperty(PROP_DEPENDENT_REQUIRED, propertyName, additionalRequiredPropertyNames, LIST_OF_STRING_CONVERTER);
        return this;
    }

    @Override
    public void removeDependentRequired(String propertyName) {
        removeFromMapProperty(PROP_DEPENDENT_REQUIRED, propertyName);
    }

    @Override
    public String getContentEncoding() {
        return getProperty(PROP_CONTENT_ENCODING, STRING_CONVERTER);
    }

    @Override
    public void setContentEncoding(String contentEncoding) {
        setProperty(PROP_CONTENT_ENCODING, contentEncoding, STRING_CONVERTER);
    }

    @Override
    public String getContentMediaType() {
        return getProperty(PROP_CONTENT_MEDIA_TYPE, STRING_CONVERTER);
    }

    @Override
    public void setContentMediaType(String contentMediaType) {
        setProperty(PROP_CONTENT_MEDIA_TYPE, contentMediaType, STRING_CONVERTER);
    }

    @Override
    public Schema getContentSchema() {
        return getProperty(PROP_CONTENT_SCHEMA, SCHEMA_CONVERTER);
    }

    @Override
    public void setContentSchema(Schema contentSchema) {
        setProperty(PROP_CONTENT_SCHEMA, contentSchema, SCHEMA_CONVERTER);
    }

    @Override
    public Boolean getBooleanSchema() {
        return booleanValue;
    }

    @Override
    @Deprecated
    public void setBooleanSchema(Boolean booleanSchema) {
        throw new UnsupportedOperationException("Can't set BooleanSchema");
    }

    @Override
    public List<Object> getExamples() {
        return getListProperty(PROP_EXAMPLES, OBJECT_CONVERTER);
    }

    @Override
    public void setExamples(List<Object> examples) {
        setListProperty(PROP_EXAMPLES, examples, OBJECT_CONVERTER);
    }

    @Override
    public Schema addExample(Object example) {
        addToListProperty(PROP_EXAMPLES, example, OBJECT_CONVERTER);
        return this;
    }

    @Override
    public void removeExample(Object example) {
        removeFromListProperty(PROP_EXAMPLES, example, OBJECT_CONVERTER);
    }

    private boolean isBooleanSchema() {
        return booleanValue != null;
    }

    /**
     * Asserts that the schema is not a boolean schema
     *
     * @throws UnsupportedOperationException if this schema is a boolean schema
     */
    private void assertObjectSchema() throws UnsupportedOperationException {
        if (isBooleanSchema()) {
            throw new UnsupportedOperationException("Schema has a boolean value");
        }
    }

    @Override
    protected <T> void setProperty(String propertyName, T value, JsonWriter<T> writer) {
        assertObjectSchema();
        super.setProperty(propertyName, value, writer);
    }

    @Override
    protected <T> T getProperty(String propertyName, JsonReader<T> reader) {
        if (isBooleanSchema()) {
            return null;
        }
        return super.getProperty(propertyName, reader);
    }

    @Override
    protected <T> List<T> getListProperty(String propertyName, JsonReader<T> reader) {
        if (isBooleanSchema()) {
            return null;
        }
        return super.getListProperty(propertyName, reader);
    }

    @Override
    protected <T> void setListProperty(String propertyName, List<T> value, JsonWriter<T> writer) {
        assertObjectSchema();
        super.setListProperty(propertyName, value, writer);
    }

    @Override
    protected <T> void addToListProperty(String propertyName, T value, JsonWriter<T> writer) {
        assertObjectSchema();
        super.addToListProperty(propertyName, value, writer);
    }

    @Override
    protected <T> void removeFromListProperty(String propertyName, T toRemove, JsonReader<T> reader) {
        if (!isBooleanSchema()) {
            super.removeFromListProperty(propertyName, toRemove, reader);
        }
    }

    @Override
    protected <T> void setMapProperty(String propertyName, Map<String, T> value, JsonWriter<T> writer) {
        assertObjectSchema();
        super.setMapProperty(propertyName, value, writer);
    }

    @Override
    protected <T> Map<String, T> getMapProperty(String propertyName, JsonReader<T> reader) {
        if (isBooleanSchema()) {
            return null;
        }
        return super.getMapProperty(propertyName, reader);
    }

    @Override
    protected <T> void addToMapProperty(String propertyName, String key, T value, JsonWriter<T> writer) {
        assertObjectSchema();
        super.addToMapProperty(propertyName, key, value, writer);
    }

    @Override
    protected <T> void removeFromMapProperty(String propertyName, String key) {
        if (!isBooleanSchema()) {
            super.removeFromMapProperty(propertyName, key);
        }
    }

    //    @Override
    //    public int hashCode() {
    //        final int prime = 31;
    //        int result = super.hashCode();
    //        result = prime * result + Objects.hash(booleanValue, modCount, name, typeObservers);
    //        return result;
    //    }
    //
    //    @Override
    //    public boolean equals(Object obj) {
    //        if (this == obj)
    //            return true;
    //        if (!super.equals(obj))
    //            return false;
    //        if (getClass() != obj.getClass())
    //            return false;
    //        SchemaImpl other = (SchemaImpl) obj;
    //        return Objects.equals(booleanValue, other.booleanValue) && modCount == other.modCount
    //                && Objects.equals(name, other.name)
    //                && Objects.equals(typeObservers, other.typeObservers);
    //    }

    protected static final JsonConverter<SchemaType> SCHEMA_TYPE_CONVERTER = new JsonConverter<SchemaType>() {

        @Override
        public JsonNode toNode(SchemaType value) {
            return STRING_CONVERTER.toNode(value.toString());
        }

        @Override
        public SchemaType fromNode(JsonNode node) {
            if (!node.isTextual())
                return null;
            return SchemaType.valueOf(node.textValue().toUpperCase(Locale.ROOT));
        }
    };

    protected static final JsonConverter<Schema> SCHEMA_CONVERTER = new JsonConverter<Schema>() {

        @Override
        public JsonNode toNode(Schema value) {
            if (value instanceof SchemaImpl) {
                Boolean booleanValue = value.getBooleanSchema();
                if (booleanValue != null) {
                    return JsonNodeFactory.instance.booleanNode(booleanValue);
                } else {
                    return ((SchemaImpl) value).node;
                }
            }
            return null;
        }

        @Override
        public Schema fromNode(JsonNode node) {
            if (node.isObject()) {
                return getOrCreateFromNode((ObjectNode) node);
            } else if (node.isBoolean()) {
                return SchemaImpl.ofBoolean(node.booleanValue());
            }
            return null;
        }
    };

    protected static final JsonConverter<ExternalDocumentation> EXTERNAL_DOCUMENTATION_CONVERTER = new JsonConverter<ExternalDocumentation>() {

        @Override
        public JsonNode toNode(ExternalDocumentation value) {
            return ExternalDocsWriter.createExternalDocumentationNode(JsonNodeFactory.instance, value);
        }

        @Override
        public ExternalDocumentation fromNode(JsonNode node) {
            return ExternalDocsReader.readExternalDocs(node);
        }
    };

    protected static final JsonConverter<XML> XML_CONVERTER = new JsonConverter<XML>() {

        @Override
        public JsonNode toNode(XML value) {
            return XmlWriter.createXMLNode(JsonNodeFactory.instance, value);
        }

        @Override
        public XML fromNode(JsonNode node) {
            return XmlReader.readXML(node);
        }
    };

    protected static final JsonConverter<Discriminator> DISCRIMINATOR_CONVERTER = new JsonConverter<Discriminator>() {

        @Override
        public JsonNode toNode(Discriminator value) {
            return DiscriminatorWriter.convertDiscriminatorToNode(JsonNodeFactory.instance, value);
        }

        @Override
        public Discriminator fromNode(JsonNode node) {
            return DiscriminatorReader.readDiscriminator(node);
        }
    };

    // We could do this generically, but we only have one case where we need this
    protected static final JsonConverter<List<String>> LIST_OF_STRING_CONVERTER = new JsonConverter<List<String>>() {

        @Override
        public JsonNode toNode(List<String> value) {
            ArrayNode node = JsonNodeFactory.instance.arrayNode();
            for (String item : value) {
                node.add(item);
            }
            return node;
        }

        @Override
        public List<String> fromNode(JsonNode node) {
            if (!node.isArray()) {
                return null;
            }
            ArrayList<String> result = new ArrayList<String>(node.size());
            for (JsonNode item : node) {
                result.add(STRING_CONVERTER.fromNode(item));
            }
            return result;
        }
    };

}
