/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.config.spring.factories;

import org.mule.routing.EventMergeStrategy;
import org.mule.util.ClassUtils;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.FactoryBean;

public class CustomMergeStrategyFactoryBean implements FactoryBean<EventMergeStrategy>
{

    private String classname;
    private EventMergeStrategy ref;

    @Override
    public EventMergeStrategy getObject() throws Exception
    {
        boolean hasClass = !StringUtils.isBlank(this.classname);
        boolean hasRef = this.ref != null;

        if (hasClass && hasRef)
        {
            throw new IllegalStateException(
                "Cannot specify class and ref at the same time. These are exclusive attributes.");
        }
        else if (hasClass)
        {
            return this.instantiateStrategy(this.classname);
        }
        else if (hasRef)
        {
            return this.ref;
        }
        else
        {
            throw new IllegalStateException(
                "If <custom-merge-strategy> element is present then you have to either set class or ref attributes");
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends EventMergeStrategy> T instantiateStrategy(String classname)
    {
        Class<T> clazz = null;
        try
        {
            clazz = (Class<T>) Class.forName(classname);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Could not find class " + classname);
        }

        if (!EventMergeStrategy.class.isAssignableFrom(clazz))
        {
            throw new IllegalStateException(String.format(
                "custom-merge-strategy class %s does not implement the class %s", clazz.getCanonicalName(),
                EventMergeStrategy.class.getCanonicalName()));
        }

        try
        {
            return ClassUtils.instanciateClass(clazz);
        }
        catch (Exception e)
        {
            throw new RuntimeException(String.format(
                "Could not instantiate class %s. Please check that it has a default constructor", classname),
                e);
        }
    }

    @Override
    public Class<?> getObjectType()
    {
        return EventMergeStrategy.class;
    }

    @Override
    public boolean isSingleton()
    {
        return false;
    }

    public String getClassname()
    {
        return this.classname;
    }

    public void setClassname(String classname)
    {
        this.classname = classname;
    }

    public EventMergeStrategy getRef()
    {
        return this.ref;
    }

    public void setRef(EventMergeStrategy ref)
    {
        this.ref = ref;
    }

}
