package grails.plugin.json.view.api

import grails.plugin.json.builder.StreamingJsonBuilder
import grails.plugin.json.view.api.internal.DefaultGrailsJsonViewHelper
import grails.plugin.json.view.api.internal.DefaultHalViewHelper
import grails.plugin.json.view.api.internal.TemplateRenderer
import grails.views.api.GrailsView

/**
 * Extends default view API with additional methods
 *
 * @author Graeme Rocher
 * @since 1.0
 */
trait JsonView extends GrailsView {

    /**
     * The {@link StreamingJsonBuilder} instance
     */
    StreamingJsonBuilder json

    /**
     * Overrides the default helper with new methods specific to JSON building
     */
    private GrailsJsonViewHelper viewHelper = new DefaultGrailsJsonViewHelper(this)



    /**
     * @return The default view helper
     */
    @Override
    GrailsJsonViewHelper getG() {
        return viewHelper
    }

    /**
     * The HAL view helper
     */
    HalViewHelper hal = new DefaultHalViewHelper(this, viewHelper)

    /**
     * The template namespace
     */
    TemplateRenderer tmpl = new TemplateRenderer(viewHelper)


    public Object json(Map m) throws IOException {
        json.call m
    }

    /**
     * The empty args call will create a key whose value will be an empty JSON object:
     * <pre class="groovyTestCase">
     * new StringWriter().with { w -&gt;
     *     def json = new groovy.json.StreamingJsonBuilder(w)
     *     json.person()
     *
     *     assert w.toString() == '{"person":{}}'
     * }
     * </pre>
     *
     * @param name The name of the empty object to create
     * @throws IOException
     */
    public void json(String name) throws IOException {
        json.call name
    }

    /**
     * A list of elements as arguments to the JSON builder creates a root JSON array
     * <p>
     * Example:
     * <pre class="groovyTestCase">
     * new StringWriter().with { w -&gt;
     *   def json = new groovy.json.StreamingJsonBuilder(w)
     *   def result = json([1, 2, 3])
     *
     *   assert result == [ 1, 2, 3 ]
     *   assert w.toString() == "[1,2,3]"
     * }
     * </pre>
     *
     * @param l a list of values
     * @return a list of values
     */
    public Object json(List l) throws IOException {
        json.call l
    }

    /**
     * Varargs elements as arguments to the JSON builder create a root JSON array
     * <p>
     * Example:
     * <pre class="groovyTestCase">
     * new StringWriter().with { w -&gt;
     *   def json = new groovy.json.StreamingJsonBuilder(w)
     *   def result = json 1, 2, 3
     *
     *   assert result instanceof List
     *   assert w.toString() == "[1,2,3]"
     * }
     * </pre>

     * @param args an array of values
     * @return a list of values
     */
    public Object json(Object... args) throws IOException {
        json.call args
    }

    /**
     * A collection and closure passed to a JSON builder will create a root JSON array applying
     * the closure to each object in the collection
     * <p>
     * Example:
     * <pre class="groovyTestCase">
     * class Author {
     *      String name
     * }
     * def authors = [new Author (name: "Guillaume"), new Author (name: "Jochen"), new Author (name: "Paul")]
     *
     * new StringWriter().with { w -&gt;
     *     def json = new groovy.json.StreamingJsonBuilder(w)
     *     json authors, { Author author -&gt;
     *         name author.name
     *     }
     *
     *     assert w.toString() == '[{"name":"Guillaume"},{"name":"Jochen"},{"name":"Paul"}]'
     * }
     * </pre>
     * @param coll a collection
     * @param c a closure used to convert the objects of coll
     */
    public Object json(Iterable coll, @DelegatesTo(StreamingJsonBuilder.StreamingJsonDelegate.class) Closure c) throws IOException {
        json.call coll, c
    }

    /**
     * A closure passed to a JSON builder will create a root JSON object
     * <p>
     * Example:
     * <pre class="groovyTestCase">
     * new StringWriter().with { w -&gt;
     *   def json = new groovy.json.StreamingJsonBuilder(w)
     *   json {
     *      name "Tim"
     *      age 39
     *   }
     *
     *   assert w.toString() == '{"name":"Tim","age":39}'
     * }
     * </pre>
     *
     * @param c a closure whose method call statements represent key / values of a JSON object
     */
    public Object json(@DelegatesTo(StreamingJsonBuilder.StreamingJsonDelegate.class) Closure c) throws IOException {
        json.call c
    }

    /**
     * A name and a closure passed to a JSON builder will create a key with a JSON object
     * <p>
     * Example:
     * <pre class="groovyTestCase">
     * new StringWriter().with { w -&gt;
     *   def json = new groovy.json.StreamingJsonBuilder(w)
     *   json.person {
     *      name "Tim"
     *      age 39
     *   }
     *
     *   assert w.toString() == '{"person":{"name":"Tim","age":39}}'
     * }
     * </pre>
     *
     * @param name The key for the JSON object
     * @param c a closure whose method call statements represent key / values of a JSON object
     */
    public void json(String name, @DelegatesTo(StreamingJsonBuilder.StreamingJsonDelegate.class) Closure c) throws IOException {
        json.call name, c
    }

    /**
     * A name, a collection and closure passed to a JSON builder will create a root JSON array applying
     * the closure to each object in the collection
     * <p>
     * Example:
     * <pre class="groovyTestCase">
     * class Author {
     *      String name
     * }
     * def authors = [new Author (name: "Guillaume"), new Author (name: "Jochen"), new Author (name: "Paul")]
     *
     * new StringWriter().with { w -&gt;
     *     def json = new groovy.json.StreamingJsonBuilder(w)
     *     json.people authors, { Author author -&gt;
     *         name author.name
     *     }
     *
     *     assert w.toString() == '{"people":[{"name":"Guillaume"},{"name":"Jochen"},{"name":"Paul"}]}'
     * }
     * </pre>
     * @param coll a collection
     * @param c a closure used to convert the objects of coll
     */
    public void json(String name, Iterable coll, @DelegatesTo(StreamingJsonBuilder.StreamingJsonDelegate.class) Closure c) throws IOException {
        json.call name, coll, c
    }

    /**
     * If you use named arguments and a closure as last argument,
     * the key/value pairs of the map (as named arguments)
     * and the key/value pairs represented in the closure
     * will be merged together &mdash;
     * the closure properties overriding the map key/values
     * in case the same key is used.
     *
     * <pre class="groovyTestCase">
     * new StringWriter().with { w -&gt;
     *     def json = new groovy.json.StreamingJsonBuilder(w)
     *     json.person(name: "Tim", age: 35) { town "Manchester" }
     *
     *     assert w.toString() == '{"person":{"name":"Tim","age":35,"town":"Manchester"}}'
     * }
     * </pre>
     *
     * @param name The name of the JSON object
     * @param map The attributes of the JSON object
     * @param callable Additional attributes of the JSON object represented by the closure
     * @throws IOException
     */
    public void json(String name, Map map, @DelegatesTo(StreamingJsonBuilder.StreamingJsonDelegate.class) Closure callable) throws IOException {
        json.call name, map, callable
    }
}