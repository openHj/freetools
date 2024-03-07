package com.xfree.utils;

import cn.hutool.core.util.ReflectUtil;
import com.xfree.anno.TreeConfig;
import com.xfree.domain.Tree;
import com.xfree.enums.TreeConfigType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 树形工具类
 * @author hj
 * @date 2024年03月06日 11:16
 */
public class TreeUtil {
    public static List<Tree> build(List<?> list) {
        List<Tree> sourceList = new ArrayList<>();
        for (Object o : list) {
            if (!hasTreeAnnotation(o)) {
                throw new RuntimeException("对象 " + o.getClass().getName() + " 中没有带有 @TreeConfig 注解的字段。");
            }
            // 通过注解构建树节点对象
            Tree tree = buildTreeByAnno(o);
            sourceList.add(tree);
        }
        List<Tree> result = new ArrayList<>();

        // 先找到所有的一级节点
        List<Long> idList = sourceList.stream().map(Tree::getId).collect(Collectors.toList());
        for (Tree tree : sourceList) {
            if (!idList.contains(tree.getParentId())) {
                result.add(tree);
            }
        }
        // 构建子节点
        for (Tree root : result) {
            buildChildren(root, sourceList);
        }
        return result;
    }


    /**
     * 是否有TreeConfig注解
     * @author hj
     * @date 2024/3/6 16:39
     * @param o
     * @return boolean
     */
    private static boolean hasTreeAnnotation(Object o) {
        Field[] fields = ReflectUtil.getFields(o.getClass());
        for (Field field : fields) {
            if (field.isAnnotationPresent(TreeConfig.class)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 通过注解构建树节点对象
     * @param o
     * @return
     */
    private static Tree buildTreeByAnno(Object o) {
        Tree tree = new Tree();
        Field[] fields = ReflectUtil.getFields(o.getClass());
        for (Field field : fields) {
            TreeConfig annotation = field.getAnnotation(TreeConfig.class);
            if (annotation != null) {
                TreeConfigType value = annotation.value();
                switch (value) {
                    case PARENT_ID:
                        tree.setParentId((Long) ReflectUtil.getFieldValue(o, field));
                        break;
                    case ID:
                        tree.setId((Long) ReflectUtil.getFieldValue(o, field));
                        break;
                    case NAME:
                        tree.setName((String) ReflectUtil.getFieldValue(o, field));
                        break;
                    case SORT:
                        tree.setSort((Integer) ReflectUtil.getFieldValue(o, field));
                        break;
                    case CODE:
                        tree.setCode((String) ReflectUtil.getFieldValue(o, field));
                        break;
                    default:
                        break;
                }
            }
        }
        return tree;
    }


    /**
     * 递归构建树节点
     * @author hj
     * @date 2024/3/6 17:52
     * @param parentNode
     * @param sourceList
     */
    private static void buildChildren(Tree parentNode, List<Tree> sourceList) {
        List<Tree> children = new ArrayList<>();
        for (Tree tree : sourceList) {
            if (parentNode.getId().equals(tree.getParentId())) {
                buildChildren(tree, sourceList);
                children.add(tree);
            }
        }
        children.sort(Comparator.comparingInt(Tree::getSort));
        parentNode.setChildren(children);
    }

}
