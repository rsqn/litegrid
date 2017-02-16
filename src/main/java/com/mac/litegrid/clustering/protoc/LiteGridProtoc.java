// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: LiteGrid.proto

package com.mac.litegrid.clustering.protoc;

public final class LiteGridProtoc {
  private LiteGridProtoc() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface NetworkPacketOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required string uid = 101;
    /**
     * <code>required string uid = 101;</code>
     */
    boolean hasUid();
    /**
     * <code>required string uid = 101;</code>
     */
    java.lang.String getUid();
    /**
     * <code>required string uid = 101;</code>
     */
    com.google.protobuf.ByteString
        getUidBytes();

    // required int64 sequence = 102;
    /**
     * <code>required int64 sequence = 102;</code>
     */
    boolean hasSequence();
    /**
     * <code>required int64 sequence = 102;</code>
     */
    long getSequence();

    // required bytes data = 403;
    /**
     * <code>required bytes data = 403;</code>
     */
    boolean hasData();
    /**
     * <code>required bytes data = 403;</code>
     */
    com.google.protobuf.ByteString getData();
  }
  /**
   * Protobuf type {@code litegrid.NetworkPacket}
   */
  public static final class NetworkPacket extends
      com.google.protobuf.GeneratedMessage
      implements NetworkPacketOrBuilder {
    // Use NetworkPacket.newBuilder() to construct.
    private NetworkPacket(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private NetworkPacket(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final NetworkPacket defaultInstance;
    public static NetworkPacket getDefaultInstance() {
      return defaultInstance;
    }

    public NetworkPacket getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private NetworkPacket(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 810: {
              bitField0_ |= 0x00000001;
              uid_ = input.readBytes();
              break;
            }
            case 816: {
              bitField0_ |= 0x00000002;
              sequence_ = input.readInt64();
              break;
            }
            case 3226: {
              bitField0_ |= 0x00000004;
              data_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.mac.litegrid.clustering.protoc.LiteGridProtoc.internal_static_litegrid_NetworkPacket_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.mac.litegrid.clustering.protoc.LiteGridProtoc.internal_static_litegrid_NetworkPacket_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket.class, com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket.Builder.class);
    }

    public static com.google.protobuf.Parser<NetworkPacket> PARSER =
        new com.google.protobuf.AbstractParser<NetworkPacket>() {
      public NetworkPacket parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new NetworkPacket(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<NetworkPacket> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required string uid = 101;
    public static final int UID_FIELD_NUMBER = 101;
    private java.lang.Object uid_;
    /**
     * <code>required string uid = 101;</code>
     */
    public boolean hasUid() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required string uid = 101;</code>
     */
    public java.lang.String getUid() {
      java.lang.Object ref = uid_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          uid_ = s;
        }
        return s;
      }
    }
    /**
     * <code>required string uid = 101;</code>
     */
    public com.google.protobuf.ByteString
        getUidBytes() {
      java.lang.Object ref = uid_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        uid_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    // required int64 sequence = 102;
    public static final int SEQUENCE_FIELD_NUMBER = 102;
    private long sequence_;
    /**
     * <code>required int64 sequence = 102;</code>
     */
    public boolean hasSequence() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required int64 sequence = 102;</code>
     */
    public long getSequence() {
      return sequence_;
    }

    // required bytes data = 403;
    public static final int DATA_FIELD_NUMBER = 403;
    private com.google.protobuf.ByteString data_;
    /**
     * <code>required bytes data = 403;</code>
     */
    public boolean hasData() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>required bytes data = 403;</code>
     */
    public com.google.protobuf.ByteString getData() {
      return data_;
    }

    private void initFields() {
      uid_ = "";
      sequence_ = 0L;
      data_ = com.google.protobuf.ByteString.EMPTY;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasUid()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasSequence()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasData()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeBytes(101, getUidBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeInt64(102, sequence_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        output.writeBytes(403, data_);
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(101, getUidBytes());
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(102, sequence_);
      }
      if (((bitField0_ & 0x00000004) == 0x00000004)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(403, data_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code litegrid.NetworkPacket}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacketOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.mac.litegrid.clustering.protoc.LiteGridProtoc.internal_static_litegrid_NetworkPacket_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.mac.litegrid.clustering.protoc.LiteGridProtoc.internal_static_litegrid_NetworkPacket_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket.class, com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket.Builder.class);
      }

      // Construct using com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        uid_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        sequence_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        data_ = com.google.protobuf.ByteString.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.mac.litegrid.clustering.protoc.LiteGridProtoc.internal_static_litegrid_NetworkPacket_descriptor;
      }

      public com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket getDefaultInstanceForType() {
        return com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket.getDefaultInstance();
      }

      public com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket build() {
        com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket buildPartial() {
        com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket result = new com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.uid_ = uid_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.sequence_ = sequence_;
        if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
          to_bitField0_ |= 0x00000004;
        }
        result.data_ = data_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket) {
          return mergeFrom((com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket other) {
        if (other == com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket.getDefaultInstance()) return this;
        if (other.hasUid()) {
          bitField0_ |= 0x00000001;
          uid_ = other.uid_;
          onChanged();
        }
        if (other.hasSequence()) {
          setSequence(other.getSequence());
        }
        if (other.hasData()) {
          setData(other.getData());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasUid()) {
          
          return false;
        }
        if (!hasSequence()) {
          
          return false;
        }
        if (!hasData()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.mac.litegrid.clustering.protoc.LiteGridProtoc.NetworkPacket) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required string uid = 101;
      private java.lang.Object uid_ = "";
      /**
       * <code>required string uid = 101;</code>
       */
      public boolean hasUid() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      /**
       * <code>required string uid = 101;</code>
       */
      public java.lang.String getUid() {
        java.lang.Object ref = uid_;
        if (!(ref instanceof java.lang.String)) {
          java.lang.String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          uid_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>required string uid = 101;</code>
       */
      public com.google.protobuf.ByteString
          getUidBytes() {
        java.lang.Object ref = uid_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          uid_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>required string uid = 101;</code>
       */
      public Builder setUid(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        uid_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required string uid = 101;</code>
       */
      public Builder clearUid() {
        bitField0_ = (bitField0_ & ~0x00000001);
        uid_ = getDefaultInstance().getUid();
        onChanged();
        return this;
      }
      /**
       * <code>required string uid = 101;</code>
       */
      public Builder setUidBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        uid_ = value;
        onChanged();
        return this;
      }

      // required int64 sequence = 102;
      private long sequence_ ;
      /**
       * <code>required int64 sequence = 102;</code>
       */
      public boolean hasSequence() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      /**
       * <code>required int64 sequence = 102;</code>
       */
      public long getSequence() {
        return sequence_;
      }
      /**
       * <code>required int64 sequence = 102;</code>
       */
      public Builder setSequence(long value) {
        bitField0_ |= 0x00000002;
        sequence_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required int64 sequence = 102;</code>
       */
      public Builder clearSequence() {
        bitField0_ = (bitField0_ & ~0x00000002);
        sequence_ = 0L;
        onChanged();
        return this;
      }

      // required bytes data = 403;
      private com.google.protobuf.ByteString data_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <code>required bytes data = 403;</code>
       */
      public boolean hasData() {
        return ((bitField0_ & 0x00000004) == 0x00000004);
      }
      /**
       * <code>required bytes data = 403;</code>
       */
      public com.google.protobuf.ByteString getData() {
        return data_;
      }
      /**
       * <code>required bytes data = 403;</code>
       */
      public Builder setData(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        data_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>required bytes data = 403;</code>
       */
      public Builder clearData() {
        bitField0_ = (bitField0_ & ~0x00000004);
        data_ = getDefaultInstance().getData();
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:litegrid.NetworkPacket)
    }

    static {
      defaultInstance = new NetworkPacket(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:litegrid.NetworkPacket)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_litegrid_NetworkPacket_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_litegrid_NetworkPacket_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016LiteGrid.proto\022\010litegrid\"=\n\rNetworkPac" +
      "ket\022\013\n\003uid\030e \002(\t\022\020\n\010sequence\030f \002(\003\022\r\n\004da" +
      "ta\030\223\003 \002(\014B4\n\"com.mac.litegrid.clustering" +
      ".protocB\016LiteGridProtoc"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_litegrid_NetworkPacket_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_litegrid_NetworkPacket_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_litegrid_NetworkPacket_descriptor,
              new java.lang.String[] { "Uid", "Sequence", "Data", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
