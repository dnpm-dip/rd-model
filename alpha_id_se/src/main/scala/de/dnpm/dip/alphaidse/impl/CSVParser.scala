package de.dnpm.dip.alphaidse.impl


import java.io.InputStream
import scala.io.Source
import de.dnpm.dip.coding.{
  Code,
  Coding,
  CodeSystem
}
import de.dnpm.dip.rd.model.AlphaIDSE


object CSVParser
{

  def read(
    in: InputStream,
    version: String
  ): CodeSystem[AlphaIDSE] =
    CodeSystem[AlphaIDSE](
      uri = Coding.System[AlphaIDSE].uri,
      name = "Alpha-ID-SE",
      title = Some("Alpha-ID-SE Verzeichnis"),
      date = None,
      version = Some(version),
      properties = AlphaIDSE.properties,
      concepts =
        Source.fromInputStream(in)
          .getLines()
          .map(_.split("\\|"))
          .map {
            csv =>
              CodeSystem.Concept[AlphaIDSE](
                code = Code(csv(1)),
                display = csv(7),
                version = Some(version),
                properties =
                  Map(
                    AlphaIDSE.Validity       -> 0,
                    AlphaIDSE.PrimaryCode1   -> 2,
                    AlphaIDSE.StarCode       -> 3,
                    AlphaIDSE.AdditionalCode -> 4,
                    AlphaIDSE.PrimaryCode2   -> 5,
                    AlphaIDSE.OrphaCode      -> 6
                  )
                  .map { 
                    case (prop,idx) => prop.name -> Set(csv(idx)).filterNot(_.isBlank)
                  },
                parent = None,
                children = None
              )
          }
          .toSeq
    )


}
