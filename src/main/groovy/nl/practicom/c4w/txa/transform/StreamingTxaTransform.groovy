package nl.practicom.c4w.txa.transform


import org.codehaus.groovy.runtime.StringBufferWriter

import static SectionMark.END

class StreamingTxaTransform implements TxaContentHandler, TxaSectionHandler, TxaRawContentHandler {

  public final static String EOL = System.lineSeparator()

  private Writer writer

  /**
   * Write transformation output to StringBuffer.
   * Use for testing or buffering
   */
  StreamingTxaTransform() {
    writer = new StringBufferWriter('' << '')
  }

  /**
   * Write transformation output to StringBuffer.
   * Use for testing or buffering
   * * @param sb
   */
  StreamingTxaTransform(StringBuffer sb){
    writer = new StringBufferWriter(sb)
  }

  /**
   * Write transformation output to file
   * @param filePath
   */
  StreamingTxaTransform(String filePath) {
    /* ToDo: path resolution, existence and access checks */
    writer = new File(filePath).newWriter()
  }

  /**
   * Write transformation to outputstream
   * @param os
   */
  StreamingTxaTransform(OutputStream os) {
    writer = os.newWriter()
  }

  /**
   * Write transformation output to writer
   * @param w
   */
  StreamingTxaTransform(Writer w) {
    writer = w
  }

  @Override
  void onProcessingStart(TxaContext context) {
    writer = new StringBufferWriter('' << '')
    transformInitialize(context)
  }

  @Override
  void onSectionStart(TxaContext context, SectionMark section) {
    def content = this.transformSectionStart(context, section)
    if ( content != null ){
      writer << content << EOL
    }
  }

  @Override
  void onSectionContent(TxaContext context, SectionMark section, String content) {
    def transformedContent = this.transformSectionContent(context, section, content)
    if ( transformedContent != null ){
      writer << transformedContent << EOL
    }
  }

  @Override
  void onSectionEnd(TxaContext context, SectionMark section) {
    def content = this.transformSectionEnd(context, section)
    if ( content != null ){
      writer << content << EOL
    }
  }

  @Override
  void onProcessingFinished(TxaContext context) {
    def content = this.transformFinalize(context)
    if ( content != null ){
      writer << content << EOL
    }
    writer.close()
  }

  protected String getContent(){
    writer.flush()
    return writer.toString() ?: ''
  }

  protected void clear(){
    writer = new StringBufferWriter('' << '')
  }

  /**
   *  The default implementation below performs an identity transform
   *  Override these methods to perform specific transforms.
   */

  protected String transformInitialize(TxaContext context){
    return null
  }

  protected String transformSectionStart(TxaContext context, SectionMark section){
    return section
  }

  protected String transformSectionContent(TxaContext context, SectionMark section, String content){
    return content
  }

  protected String transformSectionEnd(TxaContext context, SectionMark section){
    // NOTE: in case sections are nested and the current line terminates a parent section
    // a call is received for each section! So only return the [END] marker when the reaching
    // the top-level section to be terminated
    return section.requiresExplicitEnd() && context.currentLine.isSectionEnd()? END : null

  }

  protected String transformFinalize(TxaContext context){
    return null
  }
}
