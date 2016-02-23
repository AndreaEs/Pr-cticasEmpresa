/*=========================================================================

  Copyright Brigham and Women's Hospital (BWH) All Rights Reserved.

  See COPYRIGHT.txt
  or http://www.slicer.org/copyright/copyright.txt for details.

  Program:   vtkITK
  Module:    $HeadURL: http://svn.slicer.org/Slicer4/trunk/Libs/vtkITK/vtkITKGradientAnisotropicDiffusionImageFilter.h $
  Date:      $Date: 2014-05-02 23:49:48 +0200 (vie, 02 may 2014) $
  Version:   $Revision: 23121 $

==========================================================================*/

#ifndef __vtkITKGradientAnisotropicDiffusionImageFilter_h
#define __vtkITKGradientAnisotropicDiffusionImageFilter_h

#include "vtkITKImageToImageFilterFF.h"
#include "itkGradientAnisotropicDiffusionImageFilter.h"

/// \brief Wrapper class around itk::GradientAnisotropicDiffusionImageFilterImageFilter.
///
/// vtkITKGradientAnisotropicDiffusionImageFilter
class VTK_ITK_EXPORT vtkITKGradientAnisotropicDiffusionImageFilter : public vtkITKImageToImageFilterFF
{
 public:
  static vtkITKGradientAnisotropicDiffusionImageFilter *New();
  vtkTypeMacro(vtkITKGradientAnisotropicDiffusionImageFilter, vtkITKImageToImageFilterFF);

  double GetTimeStep ()
  {
    DelegateITKOutputMacro(GetTimeStep) ;
  };

  double GetConductanceParameter ()
  {
    DelegateITKOutputMacro(GetConductanceParameter) ;
  };

  unsigned int GetNumberOfIterations ()
  {
    DelegateITKOutputMacro ( GetNumberOfIterations );
  };

  void SetNumberOfIterations( unsigned int value )
  {
    DelegateITKInputMacro ( SetNumberOfIterations, value );
  };

  void SetTimeStep ( double value )
  {
    DelegateITKInputMacro ( SetTimeStep, value );
  };

  void SetConductanceParameter ( double value )
  {
    DelegateITKInputMacro ( SetConductanceParameter, value );
  };

protected:
  typedef itk::GradientAnisotropicDiffusionImageFilter<Superclass::InputImageType,Superclass::InputImageType> ImageFilterType;
  vtkITKGradientAnisotropicDiffusionImageFilter() : Superclass ( ImageFilterType::New() ){};
  ~vtkITKGradientAnisotropicDiffusionImageFilter() {};
  ImageFilterType* GetImageFilterPointer() { return dynamic_cast<ImageFilterType*> ( m_Filter.GetPointer() ); }


private:
  vtkITKGradientAnisotropicDiffusionImageFilter(const vtkITKGradientAnisotropicDiffusionImageFilter&);  /// Not implemented.
  void operator=(const vtkITKGradientAnisotropicDiffusionImageFilter&);  /// Not implemented.
};

#endif
