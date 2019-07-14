package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.meta.ClarionStringMixins
import nl.practicom.c4w.txa.model.TxaRoot

import java.nio.file.Paths

class TxaParser {

        static {
            // Add some clarion support to String class
            ClarionStringMixins.initialize()
        }

        TxaRoot parse(URL txaFileURL) {
            return this.parse(Paths.get(txaFileURL.toURI()).toString())
        }

        TxaRoot parse(String txaFilePath){
        def reader  =  new TxaReader(txaFilePath)
        def firstLine = reader.readLine()
        if (firstLine.isSectionMark()) {
            switch (firstLine.asSectionMark()){
                case SectionMark.APPLICATION:
                    return new ApplicationParser().parse(reader)
                case SectionMark.MODULE:
                    return new ModuleParser().parse(reader)
                case SectionMark.PROGRAM:
                    return new ProgramParser().parse(reader)
                case SectionMark.PROCEDURE:
                    return new ProcedureParser().parse(reader)
                default:
                    throw new RuntimeException("Invalid TXA root")
            }
        } else {
            throw new RuntimeException("Invalid TXA. TXA should start with a section but found : ${firstLine}")
        }
    }
}
