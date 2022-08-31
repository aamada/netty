/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.buffer;

/**
 * Implementations are responsible to allocate buffers. Implementations of this interface are expected to be
 * thread-safe.
 */
public interface ByteBufAllocator {

    ByteBufAllocator DEFAULT = ByteBufUtil.DEFAULT_ALLOCATOR;

    /**
     * Allocate a {@link ByteBuf}. If it is a direct or heap buffer
     * depends on the actual implementation.
     *
     * 是直接内存， 还是堆内存， 取决于， 子类的实现
     */
    ByteBuf buffer();

    /**
     * Allocate a {@link ByteBuf} with the given initial capacity.
     * If it is a direct or heap buffer depends on the actual implementation.
     *
     * 给定一个初始的容量， 去初始化一上ByteBuf
     */
    ByteBuf buffer(int initialCapacity);

    /**
     * Allocate a {@link ByteBuf} with the given initial capacity and the given
     * maximal capacity. If it is a direct or heap buffer depends on the actual
     * implementation.
     *
     * 初始化容量
     * 最大容量
     */
    ByteBuf buffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a {@link ByteBuf}, preferably a direct buffer which is suitable for I/O.
     *
     * 更加倾向于直接内存， 分配ByteBuf
     */
    ByteBuf ioBuffer();

    /**
     * Allocate a {@link ByteBuf}, preferably a direct buffer which is suitable for I/O.
     *
     * 更加倾向于直接内存， 分配ByteBuf
     */
    ByteBuf ioBuffer(int initialCapacity);

    /**
     * Allocate a {@link ByteBuf}, preferably a direct buffer which is suitable for I/O.
     *
     * 更加倾向于直接内存， 分配ByteBuf
     */
    ByteBuf ioBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a heap {@link ByteBuf}.
     *
     * 堆内存
     */
    ByteBuf heapBuffer();

    /**
     * Allocate a heap {@link ByteBuf} with the given initial capacity.
     *
     * 堆内存
     */
    ByteBuf heapBuffer(int initialCapacity);

    /**
     * Allocate a heap {@link ByteBuf} with the given initial capacity and the given
     * maximal capacity.
     *
     * 堆内存, 初始大小， 最大
     */
    ByteBuf heapBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a direct {@link ByteBuf}.
     *
     * 直接内存
     */
    ByteBuf directBuffer();

    /**
     * Allocate a direct {@link ByteBuf} with the given initial capacity.
     *
     * 直接内存
     */
    ByteBuf directBuffer(int initialCapacity);

    /**
     * Allocate a direct {@link ByteBuf} with the given initial capacity and the given
     * maximal capacity.
     *
     * 直接内存
     */
    ByteBuf directBuffer(int initialCapacity, int maxCapacity);

    /**
     * Allocate a {@link CompositeByteBuf}.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    CompositeByteBuf compositeBuffer();

    /**
     * Allocate a {@link CompositeByteBuf} with the given maximum number of components that can be stored in it.
     * If it is a direct or heap buffer depends on the actual implementation.
     */
    CompositeByteBuf compositeBuffer(int maxNumComponents);

    /**
     * Allocate a heap {@link CompositeByteBuf}.
     */
    CompositeByteBuf compositeHeapBuffer();

    /**
     * Allocate a heap {@link CompositeByteBuf} with the given maximum number of components that can be stored in it.
     */
    CompositeByteBuf compositeHeapBuffer(int maxNumComponents);

    /**
     * Allocate a direct {@link CompositeByteBuf}.
     */
    CompositeByteBuf compositeDirectBuffer();

    /**
     * Allocate a direct {@link CompositeByteBuf} with the given maximum number of components that can be stored in it.
     */
    CompositeByteBuf compositeDirectBuffer(int maxNumComponents);

    /**
     * Returns {@code true} if direct {@link ByteBuf}'s are pooled
     */
    boolean isDirectBufferPooled();

    /**
     * Calculate the new capacity of a {@link ByteBuf} that is used when a {@link ByteBuf} needs to expand by the
     * {@code minNewCapacity} with {@code maxCapacity} as upper-bound.
     */
    int calculateNewCapacity(int minNewCapacity, int maxCapacity);
 }
