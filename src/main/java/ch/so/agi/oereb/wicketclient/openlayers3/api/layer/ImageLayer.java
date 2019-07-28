package ch.so.agi.oereb.wicketclient.openlayers3.api.layer;

import org.apache.wicket.model.IModel;
import org.wicketstuff.openlayers3.api.layer.Layer;

import ch.so.agi.oereb.wicketclient.openlayers3.api.source.single.ImageWms;

public class ImageLayer extends Layer {

    /**
     * Tile source for this layer.
     */
    private ImageWms source; // was TileSource

    /**
     * The title for this layer.
     */
    private String title;

    /**
     * Creates a new instance.
     *
     * @param source
     *         The source of data for this layer
     */
//    public ImageLayer(ImageWms source) {
//        this(null, source);
//    }

    /**
     * Creates a new instance.
     *
     * @param title
     *         The title for the layer
     * @param source
     *         The source of data for this layer
     */
    public ImageLayer(String title, ImageWms source) {
        super();

        this.title = title;
        setSource(source);
    }

    /**
     * Creates a new instance.
     *
     * @param title
     *         The title for the layer
     * @param source
     *         The source of data for this layer
     */
    public ImageLayer(String title, ImageWms source, IModel<Boolean> visibleModel) {
        super(visibleModel);

        this.title = title;
        setSource(source);
    }

    /**
     * Returns the title for this layer.
     *
     * @return Title for this layer
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title for this layer.
     *
     * @param title
     *         New value
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the title for this layer.
     *
     * @param title
     *         New value
     * @return This instance
     */
    public ImageLayer title(String title) {
        setTitle(title);
        return this;
    }

    /**
     * Returns the source for this tile layer.
     *
     * @return Source for the layer
     */
    public ImageWms getSource() {
        return source;
    }

    /**
     * Sets the source for this tile layer.
     *
     * @param source Source for the layer
     */
    public void setSource(ImageWms source) {
        this.source = source;
    }

    /**
     * Sets the source for this layer.
     *
     * @param source
     *         New value
     * @return this instance
     */
    public ImageLayer source(ImageWms source) {
        setSource(source);
        return this;
    }

    @Override
    public String getJsType() {
        return "ol.layer.Image";
    }

    @Override
    public String renderJs() {

        StringBuilder builder = new StringBuilder();

        builder.append("{");

        if (title != null) {
            builder.append("'title': '" + getTitle() + "',");
        }

        if (getVisibleModel() != null) {
            builder.append("'visible': " + getVisibleModel().getObject() + ",");
        }

        builder.append("'source': new " + getSource().getJsType() + "(");
        builder.append(getSource().renderJs());
        builder.append(")");
        builder.append("}");

        return builder.toString();
    }

}
