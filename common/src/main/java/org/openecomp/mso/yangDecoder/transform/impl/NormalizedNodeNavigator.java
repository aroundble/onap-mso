package org.openecomp.mso.yangDecoder.transform.impl;

import com.google.common.base.Preconditions;
import org.openecomp.mso.yangDecoder.transform.api.NormalizedNodeVisitor;
import org.opendaylight.yangtools.yang.data.api.schema.*;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by 10036837 on 16-7-22.
 */
public class NormalizedNodeNavigator {
    private final NormalizedNodeVisitor visitor;

    public NormalizedNodeNavigator(NormalizedNodeVisitor visitor) {
        Preconditions.checkNotNull(visitor, "visitor should not be null");
        this.visitor = visitor;
    }

    public void navigate(String parentPath, NormalizedNode<?, ?> normalizedNode) {
        if (parentPath == null) {
            parentPath = "";
        }

        this.navigateNormalizedNode(0, parentPath, normalizedNode);

    }

    private void navigateDataContainerNode(int level, String parentPath, DataContainerNode<?> dataContainerNode) {
        this.visitor.visitNode(level, parentPath, dataContainerNode, true);
        String newParentPath = parentPath + "/" + dataContainerNode.getIdentifier().toString();
        Collection value = dataContainerNode.getValue();
        Iterator var6 = value.iterator();

        while (var6.hasNext()) {
            NormalizedNode node = (NormalizedNode) var6.next();
            if (node instanceof MixinNode && node instanceof NormalizedNodeContainer) {
                this.navigateNormalizedNodeContainerMixin(level, newParentPath, (NormalizedNodeContainer) node);
            } else {
                this.navigateNormalizedNode(level, newParentPath, node);
            }
        }
        this.visitor.visitNode(level, parentPath, dataContainerNode, false);
    }

    private void navigateOrderedNodeContainer(int level, String parentPath, OrderedNodeContainer<?> node) {
        String newParentPath = parentPath + "/" + node.getIdentifier().toString();
        Collection value = node.getValue();
        Iterator var6 = value.iterator();

        while (var6.hasNext()) {
            NormalizedNode normalizedNode = (NormalizedNode) var6.next();
            if (normalizedNode instanceof OrderedNodeContainer) {
                this.navigateOrderedNodeContainer(level, newParentPath, (OrderedNodeContainer) normalizedNode);
            } else {
                this.navigateNormalizedNode(level, newParentPath, normalizedNode);
            }
        }

    }

    private void navigateNormalizedNodeContainerMixin(int level, String parentPath, NormalizedNodeContainer<?, ?, ?> node) {

        String newParentPath = parentPath + "/" + node.getIdentifier().toString();
        Collection value = node.getValue();
        Iterator var6 = value.iterator();

        while (var6.hasNext()) {
            NormalizedNode normalizedNode = (NormalizedNode) var6.next();
            if (normalizedNode instanceof MixinNode && normalizedNode instanceof NormalizedNodeContainer) {
                this.navigateNormalizedNodeContainerMixin(level, newParentPath, (NormalizedNodeContainer) normalizedNode);
            } else {
                this.navigateNormalizedNode(level, newParentPath, normalizedNode);
            }
        }

    }

    private void navigateNormalizedNode(int level, String parentPath, NormalizedNode<?, ?> normalizedNode) {
        if (normalizedNode instanceof DataContainerNode) {
            DataContainerNode dataContainerNode = (DataContainerNode) normalizedNode;
            this.navigateDataContainerNode(level+1, parentPath, dataContainerNode);
        } else if (normalizedNode instanceof OrderedNodeContainer) {
            this.navigateOrderedNodeContainer(level, parentPath, (OrderedNodeContainer) normalizedNode);
        } else {
            this.visitor.visitNode(level + 1, parentPath, normalizedNode, false);
        }

    }
}