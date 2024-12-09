package com.sistema.farmacia.Service;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.sistema.farmacia.Model.Medicamento;
import com.sistema.farmacia.Model.Venta;
import com.sistema.farmacia.Repository.VentaRepository;
import org.springframework.stereotype.Service;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import com.itextpdf.kernel.colors.ColorConstants;

@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final MedicamentoService medicamentoService;

    public VentaService(VentaRepository ventaRepository, MedicamentoService medicamentoService) {
        this.ventaRepository = ventaRepository;
        this.medicamentoService = medicamentoService;
    }

    public List<Venta> listarVentas() {
        return ventaRepository.findAllWithRelations(); // Asegurarse de cargar relaciones
    }

    public Optional<Venta> obtenerVentaPorId(Integer id) {
        return ventaRepository.findById(id);
    }

    public Venta registrarVenta(Venta venta) {
        if (venta.getCliente() == null || venta.getCliente().getId() == null) {
            throw new RuntimeException("Cliente inválido en la venta.");
        }

        if (venta.getMedicamento() == null || venta.getMedicamento().getId() == null) {
            throw new RuntimeException("Medicamento inválido en la venta.");
        }

        // Obtener el medicamento desde el servicio
        Medicamento medicamento = medicamentoService.obtenerMedicamentoPorId(venta.getMedicamento().getId())
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));

        // Validar el stock del medicamento
        if (medicamento.getCantidad() < venta.getCantidad()) {
            throw new RuntimeException("Stock insuficiente para el medicamento: " + medicamento.getNombre());
        }

        // Actualizar el stock del medicamento
        medicamento.setCantidad(medicamento.getCantidad() - venta.getCantidad());
        medicamentoService.registrarMedicamento(medicamento);

        // Registrar la venta
        return ventaRepository.save(venta);
    }


    public void eliminarVenta(Integer id) {
        ventaRepository.deleteById(id);
    }

    public byte[] generarPdf(Integer id) {
        Optional<Venta> ventaOptional = ventaRepository.findById(id);

        if (ventaOptional.isEmpty()) {
            throw new RuntimeException("No se encontró la venta con ID " + id);
        }

        Venta venta = ventaOptional.get();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            com.itextpdf.kernel.pdf.PdfDocument pdfDocument = new com.itextpdf.kernel.pdf.PdfDocument(writer);

            // Crear una nueva página y dibujar el fondo verde
            pdfDocument.addNewPage();
            PdfCanvas backgroundCanvas = new PdfCanvas(pdfDocument.getPage(1));
            DeviceRgb greenBackground = new DeviceRgb(204, 255, 204); // Verde claro
            backgroundCanvas.setFillColor(greenBackground);
            backgroundCanvas.rectangle(0, 0, pdfDocument.getDefaultPageSize().getWidth(), pdfDocument.getDefaultPageSize().getHeight());
            backgroundCanvas.fill();

            Document document = new Document(pdfDocument);

            // Configuración de fuentes
            PdfFont headerFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

            // Agregar el logo
            String logoPath = "src/main/resources/static/img/logofarmacia.jpg";
            ImageData imageData = ImageDataFactory.create(logoPath);
            Image logo = new Image(imageData);
            logo.setWidth(100);
            logo.setHeight(100);
            logo.setFixedPosition(pdfDocument.getDefaultPageSize().getWidth() - 120, pdfDocument.getDefaultPageSize().getHeight() - 120);
            document.add(logo);

            // Encabezado
            document.add(new Paragraph("Pharma FuturMagica")
                    .setFont(headerFont)
                    .setFontSize(24)
                    .setFontColor(ColorConstants.BLACK) // Texto negro
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            document.add(new Paragraph("Reporte de Venta")
                    .setFont(headerFont)
                    .setFontSize(20)
                    .setFontColor(ColorConstants.BLACK) // Texto negro
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Detalles de la venta
            document.add(new Paragraph("ID de Venta: " + venta.getId())
                    .setFont(regularFont)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.BLACK));
            document.add(new Paragraph("Cliente: " + venta.getCliente().getNombre() + " " + venta.getCliente().getApellido())
                    .setFont(regularFont)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.BLACK));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            document.add(new Paragraph("Fecha: " + venta.getFecha().format(formatter))
                    .setFont(regularFont)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.BLACK));

            document.add(new Paragraph("\n"));

            // Crear tabla
            Table table = new Table(new float[]{4, 2, 2}).useAllAvailableWidth();
            DeviceRgb headerColor = new DeviceRgb(34, 139, 34); // Verde oscuro
            DeviceRgb borderColor = new DeviceRgb(0, 0, 0); // Negro

            table.addHeaderCell(new Paragraph("Medicamento").setBold()
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.WHITE) // Cabecera en blanco
                    .setBackgroundColor(headerColor)
                    .setBorder(new SolidBorder(borderColor, 1)));
            table.addHeaderCell(new Paragraph("Cantidad").setBold()
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.WHITE) // Cabecera en blanco
                    .setBackgroundColor(headerColor)
                    .setBorder(new SolidBorder(borderColor, 1)));
            table.addHeaderCell(new Paragraph("Precio").setBold()
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setFontColor(ColorConstants.WHITE) // Cabecera en blanco
                    .setBackgroundColor(headerColor)
                    .setBorder(new SolidBorder(borderColor, 1)));

            // Agregar datos a la tabla
            table.addCell(new Paragraph(venta.getMedicamento().getNombre())
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setFontColor(ColorConstants.BLACK) // Texto negro
                    .setBorder(new SolidBorder(borderColor, 1)));
            table.addCell(new Paragraph(String.valueOf(venta.getCantidad()))
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setFontColor(ColorConstants.BLACK) // Texto negro
                    .setBorder(new SolidBorder(borderColor, 1)));
            table.addCell(new Paragraph(String.valueOf(venta.getPrecio()))
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setFontColor(ColorConstants.BLACK) // Texto negro
                    .setBorder(new SolidBorder(borderColor, 1)));

            document.add(table);

            // Total
            document.add(new Paragraph("\nTotal: S/ " + venta.getTotal())
                    .setFont(headerFont)
                    .setFontSize(16)
                    .setFontColor(ColorConstants.BLACK) // Texto negro
                    .setMarginTop(20));

            // Pie de página
            document.add(new Paragraph("\nPharma FuturMagica - Gracias por su compra")
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setFontColor(ColorConstants.BLACK) // Texto negro
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF de la venta.", e);
        }
    }
}